package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.DailyExpensesDTO;
import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.MonthCategoryAmountDTO;
import com.example.budgetkeeperspring.dto.PurchaseInfoDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void matchPurchaseInfo_updatesNoteOnMatchingExpense() {
        Expense expense = new Expense();
        expense.setAmount(BigDecimal.valueOf(-45.0));
        expense.setTransactionDate(LocalDate.of(2026, 2, 9));
        expense.setCategory(new Category("Test"));

        when(expenseRepository.findAllByAmountAndTransactionDateBetween(
                any(BigDecimal.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        PurchaseInfoDTO dto = new PurchaseInfoDTO("45.0", "KARMA DLA PTAKÓW 10kg", "9.02.2026, 09:02", "2026-03-31 17:48:36");
        boolean result = expenseService.matchPurchaseInfo(dto);

        assertTrue(result);
        assertEquals("KARMA DLA PTAKÓW 10kg", expense.getNote());
        verify(expenseRepository).save(expense);
    }

    @Test
    void matchPurchaseInfo_returnsFalseWhenNoMatchFound() {
        when(expenseRepository.findAllByAmountAndTransactionDateBetween(
                any(BigDecimal.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        PurchaseInfoDTO dto = new PurchaseInfoDTO("45.0", "KARMA DLA PTAKÓW 10kg", "9.02.2026, 09:02", "2026-03-31 17:48:36");
        boolean result = expenseService.matchPurchaseInfo(dto);

        assertFalse(result);
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void getLivingExpensesComparison_groupsByYearAndMonth() {
        Category biedronka = new Category("Biedronka");
        Category rachunki = new Category("Rachunki");

        Expense e1 = new Expense();
        e1.setCategory(biedronka);
        e1.setAmount(BigDecimal.valueOf(-200));
        e1.setTransactionDate(LocalDate.of(2023, 3, 5));

        Expense e2 = new Expense();
        e2.setCategory(rachunki);
        e2.setAmount(BigDecimal.valueOf(-150));
        e2.setTransactionDate(LocalDate.of(2023, 3, 15));

        Expense e3 = new Expense();
        e3.setCategory(biedronka);
        e3.setAmount(BigDecimal.valueOf(-300));
        e3.setTransactionDate(LocalDate.of(2024, 3, 10));

        when(expenseRepository.findAllByCategoryNameIn(List.of("Biedronka", "Rachunki")))
                .thenReturn(List.of(e1, e2, e3));

        Map<Integer, List<MonthCategoryAmountDTO>> result =
                expenseService.getLivingExpensesComparison(List.of("Biedronka", "Rachunki"));

        assertEquals(2, result.size());

        List<MonthCategoryAmountDTO> month2023 = result.get(2023);
        assertNotNull(month2023);
        assertEquals(1, month2023.size());
        assertEquals(3, month2023.get(0).getMonth());
        assertEquals(0, month2023.get(0).getAmount().compareTo(BigDecimal.valueOf(350)));

        List<MonthCategoryAmountDTO> month2024 = result.get(2024);
        assertNotNull(month2024);
        assertEquals(1, month2024.size());
        assertEquals(0, month2024.get(0).getAmount().compareTo(BigDecimal.valueOf(300)));
    }

    @Test
    void getLivingExpensesComparison_emptyCategories_returnsEmptyMap() {
        Map<Integer, List<MonthCategoryAmountDTO>> result =
                expenseService.getLivingExpensesComparison(Collections.emptyList());

        assertTrue(result.isEmpty());
        verify(expenseRepository, never()).findAllByCategoryNameIn(any());
    }

    @Test
    void getLivingExpensesComparison_ignoresPositiveAmounts() {
        Category cat = new Category("Biedronka");

        Expense expense = new Expense();
        expense.setCategory(cat);
        expense.setAmount(BigDecimal.valueOf(-100));
        expense.setTransactionDate(LocalDate.of(2024, 1, 10));

        Expense income = new Expense();
        income.setCategory(cat);
        income.setAmount(BigDecimal.valueOf(500));
        income.setTransactionDate(LocalDate.of(2024, 1, 20));

        when(expenseRepository.findAllByCategoryNameIn(List.of("Biedronka")))
                .thenReturn(List.of(expense, income));

        Map<Integer, List<MonthCategoryAmountDTO>> result =
                expenseService.getLivingExpensesComparison(List.of("Biedronka"));

        List<MonthCategoryAmountDTO> months = result.get(2024);
        assertNotNull(months);
        assertEquals(1, months.size());
        assertEquals(0, months.get(0).getAmount().compareTo(BigDecimal.valueOf(100)));
    }

    @Test
    void getLivingExpensesComparison_resultIsOrderedByYear() {
        Category cat = new Category("Biedronka");

        Expense e2024 = new Expense();
        e2024.setCategory(cat);
        e2024.setAmount(BigDecimal.valueOf(-100));
        e2024.setTransactionDate(LocalDate.of(2024, 5, 1));

        Expense e2022 = new Expense();
        e2022.setCategory(cat);
        e2022.setAmount(BigDecimal.valueOf(-200));
        e2022.setTransactionDate(LocalDate.of(2022, 5, 1));

        Expense e2023 = new Expense();
        e2023.setCategory(cat);
        e2023.setAmount(BigDecimal.valueOf(-150));
        e2023.setTransactionDate(LocalDate.of(2023, 5, 1));

        when(expenseRepository.findAllByCategoryNameIn(List.of("Biedronka")))
                .thenReturn(List.of(e2024, e2022, e2023));

        Map<Integer, List<MonthCategoryAmountDTO>> result =
                expenseService.getLivingExpensesComparison(List.of("Biedronka"));

        List<Integer> years = new ArrayList<>(result.keySet());
        assertEquals(List.of(2022, 2023, 2024), years);
    }
}
