package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.CurrentMonthMoneyAmountDTO;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.entity.MoneyAmount;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import com.example.budgetkeeperspring.repository.MoneyAmountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Autowired
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
    void addMoneyForCurrentMonth_whenExists() {
        moneyAmountService.addMoneyAmountForCurrentMonth(Map.of("amount", "300"));

        ArgumentCaptor<MoneyAmount> argumentCaptor = ArgumentCaptor.forClass(MoneyAmount.class);

        verify(moneyAmountRepository, times(1)).save(argumentCaptor.capture());
        assertEquals(300, argumentCaptor.getValue().getAmount().intValue());
        assertEquals(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), argumentCaptor.getValue().getDate());
    }

    @Test
    void getMoneyForCurrentMonth_whenNotExist() {

        moneyAmountService.addMoneyAmountForCurrentMonth(Map.of("amount", "300"));
        ArgumentCaptor<MoneyAmount> argumentCaptor = ArgumentCaptor.forClass(MoneyAmount.class);

        verify(moneyAmountRepository, times(1)).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(300));
    }
}