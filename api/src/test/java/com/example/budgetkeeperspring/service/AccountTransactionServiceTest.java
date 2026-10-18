package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.entity.AccountTransaction;
import com.example.budgetkeeperspring.repository.AccountRepository;
import com.example.budgetkeeperspring.repository.AccountTransactionRepository;
import com.example.budgetkeeperspring.utils.RevolutStatementParser;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountTransactionServiceTest {

    // Mimics Revolut's export: UTF-8 bytes stored as Latin-1, C1 controls escaped as _xHHHH_.
    private static final String SHARED = """
            <sst><si><t>Rodzaj</t></si><si><t>PÅ_x0082_atnoÅ_x009b_Ä_x0087_ kartÄ</t></si>\
            <si><t>Zasilenie</t></si><si><t>Å»abka</t></si><si><t>ZAKOÅ_x0083_CZONO</t></si>\
            <si><t>COFNIÄ_x0098_TO</t></si><si><t>PLN</t></si></sst>""";

    private static final String SHEET = """
            <worksheet><sheetData>
            <row r="1"><c r="A1" t="s"><v>0</v></c></row>
            <row r="2"><c r="A2" t="s"><v>2</v></c><c r="C2"><v>46296.5</v></c><c r="D2"><v>46296.6</v></c>\
            <c r="E2" t="inlineStr"><is><t>Top-up</t></is></c><c r="F2"><v>100.0</v></c><c r="G2"><v>0.0</v></c>\
            <c r="H2" t="s"><v>6</v></c><c r="I2" t="s"><v>4</v></c><c r="J2"><v>100.0</v></c></row>
            <row r="3"><c r="A3" t="s"><v>1</v></c><c r="C3"><v>46297.25</v></c><c r="D3"><v>46297.3</v></c>\
            <c r="E3" t="s"><v>3</v></c><c r="F3"><v>-30.5</v></c><c r="G3"><v>0.5</v></c>\
            <c r="H3" t="s"><v>6</v></c><c r="I3" t="s"><v>4</v></c><c r="J3"><v>69.0</v></c></row>
            <row r="4"><c r="A4" t="s"><v>1</v></c><c r="C4"><v>46298.0</v></c>\
            <c r="E4" t="s"><v>3</v></c><c r="F4"><v>-9.99</v></c><c r="G4"><v>0.0</v></c>\
            <c r="H4" t="s"><v>6</v></c><c r="I4" t="s"><v>5</v></c></row>
            </sheetData></worksheet>""";

    @Test
    void parsesRevolutStatementAndComputesCashflow() throws IOException {
        List<AccountTransaction> rows = RevolutStatementParser.parse(new ByteArrayInputStream(xlsx()), 4L);

        assertEquals(3, rows.size());
        AccountTransaction card = rows.get(1);
        assertEquals("Żabka", card.getDescription());
        assertEquals("Płatność kartą", card.getType());
        assertEquals("ZAKOŃCZONO", card.getState());
        assertEquals(LocalDateTime.of(2026, 10, 2, 6, 0), card.getStartedAt());
        assertEquals(new BigDecimal("-30.50"), card.getAmount());
        assertNull(rows.get(2).getCompletedAt());
        assertEquals("COFNIĘTO", rows.get(2).getState());

        List<AccountTransactionService.CashflowMonth> cashflow = AccountTransactionService.cashflow(rows);
        assertEquals(List.of(new AccountTransactionService.CashflowMonth("PLN", "2026-10",
                new BigDecimal("100.00"), new BigDecimal("-31.00"), new BigDecimal("69.00"))), cashflow);
    }

    @Test
    void importSkipsDuplicatesAndRefreshesStateOfPendingOnes() throws IOException {
        AccountRepository accounts = mock(AccountRepository.class);
        AccountTransactionRepository repo = mock(AccountTransactionRepository.class);
        when(accounts.existsById(4L)).thenReturn(true);
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        List<AccountTransaction> stored = RevolutStatementParser.parse(new ByteArrayInputStream(xlsx()), 4L);
        AccountTransaction topUp = stored.get(0);
        AccountTransaction card = stored.get(1);
        card.setState("W TOKU");
        card.setCompletedAt(null);
        when(repo.findByAccountIdAndStartedAtBetween(eq(4L), any(), any())).thenReturn(List.of(topUp, card));

        AccountTransactionService service = new AccountTransactionService(accounts, repo);
        AccountTransactionService.ImportResult result =
                service.importRevolutStatement(4L, new MockMultipartFile("file", xlsx()));

        assertEquals(new AccountTransactionService.ImportResult(1, 1, 1), result);
        assertEquals("ZAKOŃCZONO", card.getState());
        verify(repo, times(1)).save(argThat(t -> t.getState().equals("COFNIĘTO")));
    }

    @Test
    void parsesCsvStatementAndKeepsCashflowPerCurrency() throws IOException {
        String csv = """
                Rodzaj,Produkt,Data rozpoczęcia,Data zrealizowania,Opis,Kwota,Opłata,Waluta,State,Saldo
                Wymiana,Bieżące,2026-08-24 18:34:52,2026-08-24 18:34:52,Wymiana na JPY,25,0,JPY,ZAKOŃCZONO,3736
                Płatność kartą,Bieżące,2026-10-17 16:57:27,2026-10-18 13:35:26,GitHub,-1985,20,JPY,ZAKOŃCZONO,93
                Wymiana,Bieżące,2026-08-24 18:34:52,2026-08-24 18:34:52,Wymiana na JPY,-0.16,0.00,USD,ZAKOŃCZONO,0.00
                TRADE,Inwestycje,2022-03-02 07:04:34,2022-03-02 07:04:34,Dividends,0.20,0.00,USD,ZAKOŃCZONO,2.02
                """;
        List<AccountTransaction> rows = RevolutStatementParser.parse(
                new ByteArrayInputStream(("\uFEFF" + csv).getBytes(StandardCharsets.UTF_8)), 4L);

        assertEquals(4, rows.size());
        assertEquals("Płatność kartą", rows.get(1).getType());
        assertEquals("Inwestycje", rows.get(3).getProduct());
        assertEquals(LocalDateTime.of(2026, 10, 18, 13, 35, 26), rows.get(1).getCompletedAt());
        assertNotEquals(rows.get(0).naturalKey(), rows.get(2).naturalKey(), "same exchange seen from both currencies");

        assertEquals(List.of(
                new AccountTransactionService.CashflowMonth("JPY", "2026-10", new BigDecimal("0"), new BigDecimal("-2005.00"), new BigDecimal("-2005.00")),
                new AccountTransactionService.CashflowMonth("JPY", "2026-08", new BigDecimal("25.00"), new BigDecimal("0"), new BigDecimal("25.00")),
                new AccountTransactionService.CashflowMonth("USD", "2026-08", new BigDecimal("0"), new BigDecimal("-0.16"), new BigDecimal("-0.16")),
                new AccountTransactionService.CashflowMonth("USD", "2022-03", new BigDecimal("0.20"), new BigDecimal("0"), new BigDecimal("0.20"))),
                AccountTransactionService.cashflow(rows));
    }

    @Test
    void balancesTakeLatestStatementRowAndFlagMissingHistory() {
        LocalDateTime at = LocalDateTime.of(2023, 8, 6, 11, 5, 26);
        List<AccountTransaction> rows = List.of(
                tx(3L, "USD", "Inwestycje", at, "-5.77", "0.00", "ZAKOŃCZONO"),
                tx(2L, "USD", "Inwestycje", at, "5.77", "5.77", "ZAKOŃCZONO"),
                tx(4L, "USD", "Inwestycje", at.plusDays(1), "-1.00", null, "COFNIĘTO"),
                tx(5L, "PLN", "Bieżące", at, "-10.00", "40.00", "ZAKOŃCZONO"));

        List<AccountTransactionService.StatementBalance> balances =
                AccountTransactionService.balances(rows, java.util.Map.of(4L, "Revolut"));

        assertEquals(2, balances.size());
        AccountTransactionService.StatementBalance pln = balances.get(0);
        assertEquals("PLN", pln.currency());
        assertEquals(new BigDecimal("40.00"), pln.balance());
        assertFalse(pln.matches(), "history before the first imported row is missing");

        AccountTransactionService.StatementBalance usd = balances.get(1);
        assertEquals("Revolut", usd.accountName());
        assertEquals(new BigDecimal("0.00"), usd.balance(), "id 3 and 2 tie on time; higher id is the later statement row");
        assertEquals(0, usd.computed().signum());
        assertTrue(usd.matches());
        assertEquals(at, usd.asOf());
    }

    @Test
    void transfersAreSettledOneToOneByOppositeAmountWithinDays() {
        LocalDateTime topUpAt = LocalDateTime.of(2026, 7, 18, 6, 46);
        List<AccountTransaction> statement = List.of(
                tx(1L, "PLN", "Bieżące", topUpAt, "70.00", "107.65", "ZAKOŃCZONO"),
                tx(2L, "PLN", "Bieżące", topUpAt.plusDays(10), "50.00", "57.65", "COFNIĘTO"));

        ExpenseDTO matched = transfer("2026-07-20", "-70.00");
        ExpenseDTO sameAmountAgain = transfer("2026-07-19", "-70.00");
        ExpenseDTO tooLate = transfer("2026-07-25", "-70.00");
        ExpenseDTO reverted = transfer("2026-07-28", "-50.00");
        ExpenseDTO notTransfer = ExpenseDTO.builder().amount(new BigDecimal("-70.00")).transactionDate("2026-07-18").build();

        AccountTransactionService.matchTransfers(List.of(sameAmountAgain, matched, tooLate, reverted), statement, java.util.Set.of(4L));

        assertTrue(sameAmountAgain.getTransferSettled(), "closest unused statement row");
        assertFalse(matched.getTransferSettled(), "the only top-up is already used");
        assertFalse(tooLate.getTransferSettled());
        assertFalse(reverted.getTransferSettled());
        assertNull(notTransfer.getTransferSettled());
    }

    @Test
    void splitTransferIsSettledByTheSumOfItsParts() {
        List<AccountTransaction> statement = List.of(
                tx(1L, "PLN", "Bieżące", LocalDateTime.of(2024, 6, 6, 9, 36), "50.00", "69.92", "ZAKOŃCZONO"));
        ExpenseDTO part1 = transfer("2024-06-06", "-23.76");
        ExpenseDTO part2 = transfer("2024-06-06", "-26.24");
        ExpenseDTO other = transfer("2024-06-07", "-10.00");
        part1.setTitle("Revolut**0059* Dublin");
        part2.setTitle("Revolut**0059* Dublin");

        AccountTransactionService.matchTransfers(List.of(part1, part2, other), statement, java.util.Set.of(4L));

        assertTrue(part1.getTransferSettled());
        assertTrue(part2.getTransferSettled());
        assertFalse(other.getTransferSettled());
    }

    private static ExpenseDTO transfer(String date, String amount) {
        return ExpenseDTO.builder().transactionDate(date).amount(new BigDecimal(amount))
                .sourceAccountId(5L).destinationAccountId(4L).build();
    }

    private static AccountTransaction tx(Long id, String currency, String product, LocalDateTime at,
                                         String amount, String balance, String state) {
        return AccountTransaction.builder().id(id).accountId(4L).currency(currency).product(product)
                .description("x").startedAt(at).completedAt(at).amount(new BigDecimal(amount))
                .balance(balance == null ? null : new BigDecimal(balance)).state(state).build();
    }

    @Test
    void rejectsUnknownFile() {
        assertThrows(IllegalArgumentException.class,
                () -> RevolutStatementParser.parse(new ByteArrayInputStream("not a zip".getBytes()), 4L));
    }

    private static byte[] xlsx() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(out)) {
            zip.putNextEntry(new ZipEntry("xl/sharedStrings.xml"));
            zip.write(SHARED.getBytes(StandardCharsets.UTF_8));
            zip.putNextEntry(new ZipEntry("xl/worksheets/sheet1.xml"));
            zip.write(SHEET.getBytes(StandardCharsets.UTF_8));
        }
        return out.toByteArray();
    }
}
