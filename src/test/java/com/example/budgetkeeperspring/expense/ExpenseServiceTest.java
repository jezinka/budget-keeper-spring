package com.example.budgetkeeperspring.expense;

import com.example.budgetkeeperspring.category.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Autowired
    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void findAll_withoutFilters() {

        Expense a = new Expense();
        a.setAmount(BigDecimal.valueOf(-50));
        a.setCategory(new Category("TestA"));

        Expense b = new Expense();
        b.setCategory(new Category("TestB"));
        b.setAmount(BigDecimal.valueOf(-120));

        when(expenseRepository
                .retrieveAll())
                .thenReturn(new ArrayList<Expense>(
                        Arrays.asList(a, b))
                );

        List<Expense> result = expenseService.findAll(new HashMap<>());

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
                .retrieveAll())
                .thenReturn(new ArrayList<Expense>(
                        Arrays.asList(a, b, c, d))
                );

        List<Expense> result = expenseService.findAll(Map.of(
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
                .retrieveAll())
                .thenReturn(new ArrayList<Expense>(
                        Arrays.asList(a, b))
                );

        List<Expense> result = expenseService.findAll(Map.of(
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
                .thenReturn(new ArrayList<Expense>(
                        Arrays.asList(a, b, c))
                );

        List<DailyExpenses> result = expenseService.getDailyExpenses(any(), any());

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getAmount().equals(BigDecimal.valueOf(170)) && p.getDay() == 12));
        assertTrue(result.stream().anyMatch(p -> p.getAmount().equals(BigDecimal.valueOf(20)) && p.getDay() == 13));
    }
}