package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.AccountDTO;
import com.example.budgetkeeperspring.mapper.AccountMapper;
import com.example.budgetkeeperspring.repository.AccountRepository;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service

@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final ExpenseRepository expenseRepository;
    private final AccountMapper accountMapper;

    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(accountMapper::mapToDto)
                .toList();
    }

    public List<AccountDTO> getSinkingFunds() {
        Map<Long, BigDecimal> incoming = expenseRepository.sumAmountByDestinationAccount()
                .stream().collect(Collectors.toMap(r -> (Long) r[0], r -> (BigDecimal) r[1]));
        Map<Long, BigDecimal> outgoing = expenseRepository.sumAmountBySourceAccount()
                .stream().collect(Collectors.toMap(r -> (Long) r[0], r -> (BigDecimal) r[1]));

        return accountRepository.findBySinkingFundTrue().stream().map(account -> {
            AccountDTO dto = accountMapper.mapToDto(account);
            BigDecimal base = account.getCurrentAmount() != null ? account.getCurrentAmount() : BigDecimal.ZERO;
            dto.setBalance(base
                    .add(incoming.getOrDefault(account.getId(), BigDecimal.ZERO))
                    .subtract(outgoing.getOrDefault(account.getId(), BigDecimal.ZERO)));
            return dto;
        }).toList();
    }
}
