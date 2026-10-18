package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.entity.Account;
import com.example.budgetkeeperspring.entity.AccountTransaction;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.repository.AccountRepository;
import com.example.budgetkeeperspring.repository.AccountTransactionRepository;
import com.example.budgetkeeperspring.utils.RevolutStatementParser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountTransactionService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;

    public record ImportResult(int added, int updated, int unchanged) {
    }

    /** Statement balance vs. sum of imported transactions; a mismatch means missing history or a gap between imports. */
    public record StatementBalance(Long accountId, String accountName, String currency, String product,
                                   BigDecimal balance, BigDecimal computed, LocalDateTime asOf, boolean matches) {
    }

    public record CashflowMonth(String currency, String month, BigDecimal inflow, BigDecimal outflow, BigDecimal net) {
    }

    /**
     * Imports a Revolut statement. Rows already stored (same start time, amount and description) are not
     * duplicated; only their state/completion/balance is refreshed, e.g. when a pending payment completed.
     */
    @Transactional
    public ImportResult importRevolutStatement(Long accountId, MultipartFile file) throws IOException {
        if (!accountRepository.existsById(accountId)) {
            throw new NotFoundException();
        }
        List<AccountTransaction> parsed;
        try (InputStream in = file.getInputStream()) {
            parsed = RevolutStatementParser.parse(in, accountId);
        }
        if (parsed.isEmpty()) {
            return new ImportResult(0, 0, 0);
        }

        LocalDateTime from = parsed.stream().map(AccountTransaction::getStartedAt).min(Comparator.naturalOrder()).orElseThrow();
        LocalDateTime to = parsed.stream().map(AccountTransaction::getStartedAt).max(Comparator.naturalOrder()).orElseThrow();
        Map<String, AccountTransaction> existing = accountTransactionRepository
                .findByAccountIdAndStartedAtBetween(accountId, from, to).stream()
                .collect(Collectors.toMap(AccountTransaction::naturalKey, Function.identity(), (a, b) -> a));

        int added = 0, updated = 0, unchanged = 0;
        for (AccountTransaction t : parsed) {
            AccountTransaction stored = existing.get(t.naturalKey());
            if (stored == null) {
                existing.put(t.naturalKey(), accountTransactionRepository.save(t));
                added++;
            } else if (!Objects.equals(stored.getState(), t.getState())
                    || !Objects.equals(stored.getCompletedAt(), t.getCompletedAt())) {
                stored.setState(t.getState());
                stored.setCompletedAt(t.getCompletedAt());
                stored.setBalance(t.getBalance());
                stored.setFee(t.getFee());
                updated++;
            } else {
                unchanged++;
            }
        }
        return new ImportResult(added, updated, unchanged);
    }

    /** Bank booking date of a top-up may differ from the statement date by a few days. */
    static final int TRANSFER_DAYS_TOLERANCE = 3;

    /**
     * Marks expenses that move money between the main account and an external account (e.g. a Revolut top-up)
     * as settled when the external statement has the counterpart row: opposite amount, within a few days.
     */
    public void markSettledTransfers(List<ExpenseDTO> expenses) {
        Set<Long> external = accountRepository.findBySinkingFundFalseAndDefaultAccountFalse().stream()
                .map(Account::getId).collect(Collectors.toSet());
        List<ExpenseDTO> transfers = expenses.stream().filter(e -> externalAccount(e, external) != null).toList();
        if (transfers.isEmpty()) {
            return;
        }
        LocalDate from = transfers.stream().map(AccountTransactionService::date).min(Comparator.naturalOrder()).orElseThrow();
        LocalDate to = transfers.stream().map(AccountTransactionService::date).max(Comparator.naturalOrder()).orElseThrow();
        List<AccountTransaction> candidates = accountTransactionRepository.findByAccountIdInAndStartedAtBetween(external,
                from.minusDays(TRANSFER_DAYS_TOLERANCE).atStartOfDay(), to.plusDays(TRANSFER_DAYS_TOLERANCE + 1).atStartOfDay());
        matchTransfers(transfers, candidates, external);
    }

    static void matchTransfers(List<ExpenseDTO> transfers, List<AccountTransaction> candidates, Set<Long> external) {
        Set<AccountTransaction> used = Collections.newSetFromMap(new IdentityHashMap<>());
        for (ExpenseDTO e : transfers) {
            e.setTransferSettled(claim(candidates, used, externalAccount(e, external), e.getAmount().negate(), date(e)));
        }
        // a split expense: parts share date and title, the statement holds one top-up for their sum
        transfers.stream()
                .filter(e -> !e.getTransferSettled())
                .collect(Collectors.groupingBy(e -> Arrays.asList(externalAccount(e, external), date(e), e.getTitle())))
                .forEach((key, parts) -> {
                    if (parts.size() < 2) {
                        return;
                    }
                    BigDecimal sum = parts.stream().map(ExpenseDTO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (claim(candidates, used, (Long) key.get(0), sum.negate(), (LocalDate) key.get(1))) {
                        parts.forEach(p -> p.setTransferSettled(true));
                    }
                });
    }

    /** Takes the unused completed statement row with the given amount closest to the date; each row settles one expense. */
    private static boolean claim(List<AccountTransaction> candidates, Set<AccountTransaction> used,
                                 Long account, BigDecimal amount, LocalDate date) {
        Optional<AccountTransaction> match = candidates.stream()
                .filter(t -> !used.contains(t) && t.getAccountId().equals(account)
                        && RevolutStatementParser.COMPLETED_STATES.contains(t.getState())
                        && t.getAmount().compareTo(amount) == 0
                        && daysBetween(t, date) <= TRANSFER_DAYS_TOLERANCE)
                .min(Comparator.comparingLong(t -> daysBetween(t, date)));
        match.ifPresent(used::add);
        return match.isPresent();
    }

    private static long daysBetween(AccountTransaction t, LocalDate date) {
        return Math.abs(t.getStartedAt().toLocalDate().toEpochDay() - date.toEpochDay());
    }

    private static Long externalAccount(ExpenseDTO e, Set<Long> external) {
        if (e.getAmount() == null || e.getTransactionDate() == null) {
            return null;
        }
        if (e.getAmount().signum() < 0 && external.contains(e.getDestinationAccountId())) {
            return e.getDestinationAccountId();
        }
        if (e.getAmount().signum() > 0 && external.contains(e.getSourceAccountId())) {
            return e.getSourceAccountId();
        }
        return null;
    }

    private static LocalDate date(ExpenseDTO e) {
        return LocalDate.parse(e.getTransactionDate().substring(0, 10));
    }

    public List<AccountTransaction> getTransactions(Long accountId) {
        return accountTransactionRepository.findByAccountIdOrderByStartedAtDesc(accountId);
    }

    public List<StatementBalance> getBalances() {
        Map<Long, String> names = accountRepository.findAll().stream()
                .collect(Collectors.toMap(Account::getId, Account::getName));
        // ponytail: aggregates in memory; fine for a few thousand statement rows, move to SQL if it grows
        return balances(accountTransactionRepository.findAll(), names);
    }

    static List<StatementBalance> balances(List<AccountTransaction> transactions, Map<Long, String> names) {
        Comparator<AccountTransaction> chronological = Comparator
                .comparing((AccountTransaction t) -> t.getCompletedAt() != null ? t.getCompletedAt() : t.getStartedAt())
                .thenComparing(AccountTransaction::getId, Comparator.nullsFirst(Comparator.naturalOrder()));

        Map<List<Object>, List<AccountTransaction>> groups = transactions.stream()
                .filter(t -> RevolutStatementParser.COMPLETED_STATES.contains(t.getState()))
                .collect(Collectors.groupingBy(t -> Arrays.asList(t.getAccountId(), t.getCurrency(), t.getProduct()),
                        LinkedHashMap::new, Collectors.toList()));

        return groups.values().stream()
                .map(rows -> {
                    AccountTransaction first = rows.get(0);
                    // same timestamp (e.g. a transfer in and out at once): the later statement row (higher id) wins
                    AccountTransaction last = rows.stream().filter(t -> t.getBalance() != null).max(chronological).orElse(null);
                    BigDecimal computed = rows.stream().map(t -> t.getAmount().subtract(t.getFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal balance = last == null ? null : last.getBalance();
                    return new StatementBalance(first.getAccountId(), names.get(first.getAccountId()),
                            first.getCurrency(), first.getProduct(), balance, computed,
                            last == null ? null : (last.getCompletedAt() != null ? last.getCompletedAt() : last.getStartedAt()),
                            balance != null && balance.compareTo(computed) == 0);
                })
                .sorted(Comparator.comparing(StatementBalance::accountName, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(b -> !"PLN".equals(b.currency()))
                        .thenComparing(StatementBalance::currency, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(StatementBalance::product, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    public List<CashflowMonth> getCashflow(Long accountId) {
        return cashflow(getTransactions(accountId));
    }

    /** Monthly inflow/outflow of completed transactions per currency (fees count as outflow), newest month first. */
    static List<CashflowMonth> cashflow(List<AccountTransaction> transactions) {
        // ponytail: no FX conversion; each currency gets its own cashflow, exchanges show up as in/out of a currency
        Map<String, TreeMap<YearMonth, List<BigDecimal>>> byCurrency = transactions.stream()
                .filter(t -> RevolutStatementParser.COMPLETED_STATES.contains(t.getState()))
                .collect(Collectors.groupingBy(t -> Objects.toString(t.getCurrency(), ""), TreeMap::new,
                        Collectors.groupingBy(t -> YearMonth.from(t.getStartedAt()), TreeMap::new,
                                Collectors.mapping(t -> t.getAmount().subtract(t.getFee()), Collectors.toList()))));

        List<CashflowMonth> result = new ArrayList<>();
        byCurrency.forEach((currency, months) -> months.descendingMap().forEach((month, amounts) -> {
            BigDecimal in = amounts.stream().filter(a -> a.signum() > 0).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal out = amounts.stream().filter(a -> a.signum() < 0).reduce(BigDecimal.ZERO, BigDecimal::add);
            result.add(new CashflowMonth(currency, month.toString(), in, out, in.add(out)));
        }));
        return result;
    }
}
