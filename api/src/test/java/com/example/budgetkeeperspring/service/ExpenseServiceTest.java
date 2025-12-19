package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.DailyExpensesDTO;
import com.example.budgetkeeperspring.dto.ExpenseDTO;
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

        Category unknownCategory = Category.builder()
                .id(CategoryService.UNKNOWN_CATEGORY)
                .name("Unknown")
                .build();

        Expense a = new Expense();
        a.setAmount(BigDecimal.valueOf(-50));
        a.setCategory(Category.builder().id(1L).name("TestA").build());

        Expense b = new Expense();
        b.setAmount(BigDecimal.valueOf(-120));
        b.setTransactionDate(LocalDate.of(2023, 1, 14));
        b.setTitle("test");
        b.setPayee("test_user");
        b.setCategory(unknownCategory);

        Expense c = new Expense();
        c.setAmount(BigDecimal.valueOf(120));
        c.setCategory(Category.builder().id(2L).name("TestC").build());

        Expense d = new Expense();
        d.setAmount(BigDecimal.valueOf(-120));
        d.setCategory(Category.builder().id(3L).name("TestD").build());

        when(expenseRepository
                .findAllByOrderByTransactionDateDesc())
                .thenReturn(new ArrayList<>(
                        Arrays.asList(a, b, c, d))
                );

        List<ExpenseDTO> result = expenseService.findAll(Map.of(
                "onlyEmptyCategories", true,
                "onlyExpenses", true,
                "dateFrom", "2023-01-01",
                "dateTo", "2023-01-31",
                "description", "test"));

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
    void findAll_withDateFromFilter() {
        Expense a = new Expense();
        a.setAmount(BigDecimal.valueOf(-50));
        a.setTransactionDate(LocalDate.of(2023, 1, 10));
        a.setCategory(new Category("TestA"));

        Expense b = new Expense();
        b.setAmount(BigDecimal.valueOf(-120));
        b.setTransactionDate(LocalDate.of(2023, 1, 20));
        b.setCategory(new Category("TestB"));

        Expense c = new Expense();
        c.setAmount(BigDecimal.valueOf(-80));
        c.setTransactionDate(LocalDate.of(2023, 1, 30));
        c.setCategory(new Category("TestC"));

        when(expenseRepository
                .findAllByOrderByTransactionDateDesc())
                .thenReturn(new ArrayList<>(
                        Arrays.asList(a, b, c))
                );

        List<ExpenseDTO> result = expenseService.findAll(Map.of(
                "dateFrom", "2023-01-20"));

        assertEquals(2, result.size());
    }

    @Test
    void findAll_withDateToFilter() {
        Expense a = new Expense();
        a.setAmount(BigDecimal.valueOf(-50));
        a.setTransactionDate(LocalDate.of(2023, 1, 10));
        a.setCategory(new Category("TestA"));

        Expense b = new Expense();
        b.setAmount(BigDecimal.valueOf(-120));
        b.setTransactionDate(LocalDate.of(2023, 1, 20));
        b.setCategory(new Category("TestB"));

        Expense c = new Expense();
        c.setAmount(BigDecimal.valueOf(-80));
        c.setTransactionDate(LocalDate.of(2023, 1, 30));
        c.setCategory(new Category("TestC"));

        when(expenseRepository
                .findAllByOrderByTransactionDateDesc())
                .thenReturn(new ArrayList<>(
                        Arrays.asList(a, b, c))
                );

        List<ExpenseDTO> result = expenseService.findAll(Map.of(
                "dateTo", "2023-01-20"));

        assertEquals(2, result.size());
    }

    @Test
    void findAll_withDateRangeFilter() {
        Expense a = new Expense();
        a.setAmount(BigDecimal.valueOf(-50));
        a.setTransactionDate(LocalDate.of(2023, 1, 10));
        a.setCategory(new Category("TestA"));

        Expense b = new Expense();
        b.setAmount(BigDecimal.valueOf(-120));
        b.setTransactionDate(LocalDate.of(2023, 1, 20));
        b.setCategory(new Category("TestB"));

        Expense c = new Expense();
        c.setAmount(BigDecimal.valueOf(-80));
        c.setTransactionDate(LocalDate.of(2023, 1, 30));
        c.setCategory(new Category("TestC"));

        when(expenseRepository
                .findAllByOrderByTransactionDateDesc())
                .thenReturn(new ArrayList<>(
                        Arrays.asList(a, b, c))
                );

        List<ExpenseDTO> result = expenseService.findAll(Map.of(
                "dateFrom", "2023-01-15",
                "dateTo", "2023-01-25"));

        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(-120), result.get(0).getAmount());
    }

    @Test
    void findAll_withDescriptionFilter() {
        Expense a = new Expense();
        a.setAmount(BigDecimal.valueOf(-50));
        a.setTitle("grocery shopping");
        a.setCategory(new Category("TestA"));

        Expense b = new Expense();
        b.setAmount(BigDecimal.valueOf(-120));
        b.setPayee("restaurant xyz");
        b.setCategory(new Category("TestB"));

        Expense c = new Expense();
        c.setAmount(BigDecimal.valueOf(-80));
        c.setNote("shopping for clothes");
        c.setCategory(new Category("TestC"));

        Expense d = new Expense();
        d.setAmount(BigDecimal.valueOf(-30));
        d.setTitle("books");
        d.setCategory(new Category("TestD"));

        when(expenseRepository
                .findAllByOrderByTransactionDateDesc())
                .thenReturn(new ArrayList<>(
                        Arrays.asList(a, b, c, d))
                );

        List<ExpenseDTO> result = expenseService.findAll(Map.of(
                "description", "shop"));

        assertEquals(2, result.size());
    }

    @Test
    void findAll_withAmountFilter() {
        Expense a = new Expense();
        a.setAmount(BigDecimal.valueOf(-50));
        a.setCategory(new Category("TestA"));

        Expense b = new Expense();
        b.setAmount(BigDecimal.valueOf(-120));
        b.setCategory(new Category("TestB"));

        Expense c = new Expense();
        c.setAmount(BigDecimal.valueOf(-120));
        c.setCategory(new Category("TestC"));

        when(expenseRepository
                .findAllByOrderByTransactionDateDesc())
                .thenReturn(new ArrayList<>(
                        Arrays.asList(a, b, c))
                );

        List<ExpenseDTO> result = expenseService.findAll(Map.of(
                "amount", "-120"));

        assertEquals(2, result.size());
    }

    @Test
    void findAll_withCategoryLevelFilter() {
        Category categoryLevel0 = new Category("Level0Category");
        categoryLevel0.setLevel(0);

        Category categoryLevel1 = new Category("Level1Category");
        categoryLevel1.setLevel(1);

        Expense a = new Expense();
        a.setAmount(BigDecimal.valueOf(-50));
        a.setCategory(categoryLevel0);

        Expense b = new Expense();
        b.setAmount(BigDecimal.valueOf(-120));
        b.setCategory(categoryLevel1);

        Expense c = new Expense();
        c.setAmount(BigDecimal.valueOf(-30));
        c.setCategory(categoryLevel0);

        when(expenseRepository
                .findAllByOrderByTransactionDateDesc())
                .thenReturn(new ArrayList<>(
                        Arrays.asList(a, b, c))
                );

        List<ExpenseDTO> result = expenseService.findAll(Map.of(
                "categoryLevel", "0"));

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(e -> e.getCategoryLevel() != null && e.getCategoryLevel().equals(0)));
    }
}