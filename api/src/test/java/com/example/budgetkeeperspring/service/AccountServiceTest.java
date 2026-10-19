package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.AccountDTO;
import com.example.budgetkeeperspring.entity.Account;
import com.example.budgetkeeperspring.mapper.AccountMapper;
import com.example.budgetkeeperspring.repository.AccountRepository;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    @Test
    void sinkingFundBalanceIsStartingAmountPlusIncomingMinusOutgoing() {
        AccountRepository accountRepository = mock(AccountRepository.class);
        ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
        Account holidays = Account.builder().id(1L).name("Wakacje").currentAmount(new BigDecimal("100.00")).sinkingFund(true).build();
        Account car = Account.builder().id(2L).name("Auto").sinkingFund(true).build();
        when(accountRepository.findBySinkingFundTrue()).thenReturn(List.of(holidays, car));
        when(expenseRepository.sumAmountByDestinationAccount()).thenReturn(List.<Object[]>of(
                new Object[]{1L, new BigDecimal("50.00")}, new Object[]{2L, new BigDecimal("300.00")}));
        when(expenseRepository.sumAmountBySourceAccount()).thenReturn(List.<Object[]>of(
                new Object[]{1L, new BigDecimal("20.00")}));
        AccountService service = new AccountService(accountRepository, expenseRepository, Mappers.getMapper(AccountMapper.class));

        List<AccountDTO> funds = service.getSinkingFunds();

        assertEquals(new BigDecimal("130.00"), funds.get(0).getBalance());
        // brak kwoty startowej liczy się jako zero
        assertEquals(new BigDecimal("300.00"), funds.get(1).getBalance());
    }
}
