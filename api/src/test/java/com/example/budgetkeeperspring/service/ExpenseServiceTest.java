package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.DailyExpensesDTO;
import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.FireDataDTO;
import com.example.budgetkeeperspring.dto.YearlyExpensesDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.mapper.ExpenseMapper;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    ExpenseRepository expenseRepository;

    @InjectMocks
    ExpenseService expenseService;

    @Spy
    ExpenseMapper expenseMapper = Mappers.getMapper(ExpenseMapper.class);

    @Test
    void findAll_withoutFilters() {

        Expense a = new Expense();
        a.setAmount(BigDecimal.valueOf(-50));
        a.setCategory(new Category("TestA"));

        Expense b = new Expense();
        b.setCategory(new Category("TestB"));
        b.setAmount(BigDecimal.valueOf(-120));

        when(expenseRepository
                .findAllByOrderByTransactionDateDesc())
                .thenReturn(new ArrayList<>(
                        Arrays.asList(a, b))
                );

        List<ExpenseDTO> result = expenseService.findAll(new HashMap<>());

        assertEquals(2, result.size());
    }


    @Test
    void findAll_withFilters() {

        Expense a = new Expense();
        a.setAmount(BigDecimal.valueOf(-50));
        a.setCategory(new Category("TestA"));

        Expense b = new Expense();
        b.setAmount(BigDecimal.valueOf(-120));
        b.setTransactionDate(LocalDate.of(2023, 1, 14));
        b.setTitle("test");
        b.setPayee("test_user");

        Expense c = new Expense();
        c.setAmount(BigDecimal.valueOf(120));

        Expense d = new Expense();
        d.setAmount(BigDecimal.valueOf(-120));

        when(expenseRepository
                .findAllByOrderByTransactionDateDesc())
                .thenReturn(new ArrayList<>(
                        Arrays.asList(a, b, c, d))
                );

        List<ExpenseDTO> result = expenseService.findAll(Map.of(
                "onlyEmptyCategories", true,
                "onlyExpenses", true,
                "year", 2023,
                "month", 1,
                "title", "test",
                "payee", "test_user"));

        assertEquals(1, result.size());
    }

    @Test
    void findAll_withCategoryFilter() {

        Expense a = new Expense();
        a.setAmount(BigDecimal.valueOf(-50));
        a.setCategory(new Category("TestA"));

        Expense b = new Expense();
        b.setAmount(BigDecimal.valueOf(-120));
        b.setTransactionDate(LocalDate.of(2023, 1, 14));
        b.setTitle("test");
        b.setPayee("test_user");

        when(expenseRepository
                .findAllByOrderByTransactionDateDesc())
                .thenReturn(new ArrayList<>(
                        Arrays.asList(a, b))
                );

        List<ExpenseDTO> result = expenseService.findAll(Map.of(
                "onlyEmptyCategories", false,
                "category", "TestA"));

        assertEquals(1, result.size());
    }

    @Test
    void getDailyExpenses() {

        Expense a = new Expense();
        a.setAmount(BigDecimal.valueOf(-50));
        a.setTransactionDate(LocalDate.of(2023, 1, 12));

        Expense b = new Expense();
        b.setTransactionDate(LocalDate.of(2023, 1, 12));
        b.setAmount(BigDecimal.valueOf(-120));

        Expense c = new Expense();
        c.setTransactionDate(LocalDate.of(2023, 1, 13));
        c.setAmount(BigDecimal.valueOf(-20));

        when(expenseRepository
                .findAllByTransactionDateBetween(any(), any()))
                .thenReturn(new ArrayList<>(
                        Arrays.asList(a, b, c))
                );

        List<DailyExpensesDTO> result = expenseService.getDailyExpenses(any(), any());

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getAmount().compareTo(BigDecimal.valueOf(170)) == 0 && p.getDay() == 12));
        assertTrue(result.stream().anyMatch(p -> p.getAmount().compareTo(BigDecimal.valueOf(20)) == 0 && p.getDay() == 13));
    }

    @Test
    void getFireNumber_withAnnualExpenses() {
        List<YearlyExpensesDTO> yearlyExpenses = Arrays.asList(
                new YearlyExpensesDTO(2023, BigDecimal.valueOf(10000)),
                new YearlyExpensesDTO(2023, BigDecimal.valueOf(15000)),
                new YearlyExpensesDTO(2023, BigDecimal.valueOf(20000))
        );
        when(expenseRepository.findAnnualExpensesForPreviousYears()).thenReturn(yearlyExpenses);
        when(expenseRepository.findAllByCategory_Level(2)).thenReturn(Collections.emptyList());

        FireDataDTO result = expenseService.getFireNumber();

        assertEquals(BigDecimal.valueOf(375000), result.getFireNumber());
        assertEquals(BigDecimal.ZERO, result.getInvestmentSum());
    }

    @Test
    void getFireNumber_withInvestments() {
        List<YearlyExpensesDTO> yearlyExpenses = Arrays.asList(
                new YearlyExpensesDTO(2023, BigDecimal.valueOf(10000)),
                new YearlyExpensesDTO(2023, BigDecimal.valueOf(15000)),
                new YearlyExpensesDTO(2023, BigDecimal.valueOf(20000))
        );
        List<Expense> investments = Arrays.asList(
                expenseMapper.mapToEntity(ExpenseDTO.builder().transactionDate("2023-12-23").amount(BigDecimal.valueOf(5000)).build()),
                expenseMapper.mapToEntity(ExpenseDTO.builder().transactionDate("2023-11-25").amount(BigDecimal.valueOf(7000)).build())
        );
        when(expenseRepository.findAnnualExpensesForPreviousYears()).thenReturn(yearlyExpenses);
        when(expenseRepository.findAllByCategory_Level(2)).thenReturn(investments);

        FireDataDTO result = expenseService.getFireNumber();

        assertEquals(BigDecimal.valueOf(375000), result.getFireNumber());
        assertEquals(BigDecimal.valueOf(12000), result.getInvestmentSum());
    }

    @Test
    void getFireNumber_withNoAnnualExpenses() {
        when(expenseRepository.findAnnualExpensesForPreviousYears()).thenReturn(Collections.emptyList());

        FireDataDTO result = expenseService.getFireNumber();

        assertEquals(BigDecimal.ZERO, result.getFireNumber());
        assertEquals(BigDecimal.ZERO, result.getInvestmentSum());
    }

    @Test
    void getFireNumber_withNegativeExpenses() {
        List<YearlyExpensesDTO> yearlyExpenses = Arrays.asList(
                new YearlyExpensesDTO(2023, BigDecimal.valueOf(-10000)),
                new YearlyExpensesDTO(2023, BigDecimal.valueOf(-15000)),
                new YearlyExpensesDTO(2023, BigDecimal.valueOf(-20000))
        );
        when(expenseRepository.findAnnualExpensesForPreviousYears()).thenReturn(yearlyExpenses);
        when(expenseRepository.findAllByCategory_Level(2)).thenReturn(Collections.emptyList());

        FireDataDTO result = expenseService.getFireNumber();

        assertEquals(BigDecimal.valueOf(375000), result.getFireNumber());
        assertEquals(BigDecimal.ZERO, result.getInvestmentSum());
    }
}