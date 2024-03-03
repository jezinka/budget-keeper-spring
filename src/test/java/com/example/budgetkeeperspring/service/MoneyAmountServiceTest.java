package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.CurrentMonthMoneyAmountDTO;
import com.example.budgetkeeperspring.dto.MoneyAmountDTO;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.entity.MoneyAmount;
import com.example.budgetkeeperspring.mapper.MoneyAmountMapper;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import com.example.budgetkeeperspring.repository.MoneyAmountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoneyAmountServiceTest {

    @Mock
    MoneyAmountRepository moneyAmountRepository;
    @Mock
    ExpenseRepository expenseRepository;
    @Spy
    MoneyAmountMapper moneyAmountMapper = Mappers.getMapper(MoneyAmountMapper.class);

    @InjectMocks
    MoneyAmountService moneyAmountService;

    LocalDate start;
    LocalDate end;

    @BeforeEach
    void setup() {
        start = LocalDate.of(2022, 1, 1);
        end = LocalDate.of(2022, 1, 31);
    }

    @Test
    void getForPeriodTest_noMoneyAmount() {
        CurrentMonthMoneyAmountDTO result = moneyAmountService.getForPeriod(start, end);
        assertNull(result.getExpenses());
    }

    @Test
    void getForPeriodTest() {

        Expense a = new Expense();
        a.setAmount(BigDecimal.valueOf(-50));
        Expense b = new Expense();
        b.setAmount(BigDecimal.valueOf(-120));
        Expense c = new Expense();
        c.setAmount(BigDecimal.valueOf(-30));
        Expense d = new Expense();
        d.setAmount(BigDecimal.valueOf(100));
        Expense e = new Expense();
        e.setAmount(BigDecimal.valueOf(78));
        Expense f = new Expense();
        f.setAmount(BigDecimal.valueOf(122));


        when(moneyAmountRepository.findFirstByDateOrderByCreatedAtDesc(any(LocalDate.class))).thenReturn(
                Optional.of(MoneyAmount.builder()
                        .date(start)
                        .amount(BigDecimal.valueOf(600))
                        .build()));

        when(expenseRepository
                .findAllByTransactionDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<Expense>(
                        Arrays.asList(a, b, c, d, e, f))
                );

        CurrentMonthMoneyAmountDTO result = moneyAmountService.getForPeriod(start, end);

        assertEquals(BigDecimal.valueOf(-200), result.getExpenses());
        assertEquals(BigDecimal.valueOf(300), result.getIncomes());
        assertEquals(BigDecimal.valueOf(600), result.getStart());
        assertEquals(BigDecimal.valueOf(700), result.getAccountBalance());
    }

    @Test
    void addMoneyForCurrentMonth() {
        moneyAmountService.addMoneyAmountForCurrentMonth(MoneyAmountDTO.builder()
                .amount(BigDecimal.valueOf(300))
                .date(DateUtilsService.getBeginOfCurrentMonth())
                .build());

        ArgumentCaptor<MoneyAmount> argumentCaptor = ArgumentCaptor.forClass(MoneyAmount.class);

        verify(moneyAmountRepository, times(1)).save(argumentCaptor.capture());
        assertEquals(300, argumentCaptor.getValue().getAmount().intValue());
        assertEquals(DateUtilsService.getBeginOfCurrentMonth(), argumentCaptor.getValue().getDate());
    }

    @Test
    void addMoneyAmountForNextMonth() {
        Expense b = new Expense();
        b.setAmount(BigDecimal.valueOf(-120));

        Expense f = new Expense();
        f.setAmount(BigDecimal.valueOf(122));


        when(moneyAmountRepository.findFirstByDateOrderByCreatedAtDesc(any(LocalDate.class))).thenReturn(
                Optional.of(MoneyAmount.builder()
                        .date(start)
                        .amount(BigDecimal.valueOf(100))
                        .build()));

        when(expenseRepository
                .findAllByTransactionDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<Expense>(Arrays.asList(b, f)));


        moneyAmountService.addMoneyAmountForNextMonth();

        ArgumentCaptor<MoneyAmount> argumentCaptor = ArgumentCaptor.forClass(MoneyAmount.class);

        verify(moneyAmountRepository, times(1)).save(argumentCaptor.capture());
        assertEquals(102, argumentCaptor.getValue().getAmount().intValue());
        assertEquals(3, argumentCaptor.getValue().getDate().getMonthValue());
    }
}