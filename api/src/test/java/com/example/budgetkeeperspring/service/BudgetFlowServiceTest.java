package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.SankeyDto;
import com.example.budgetkeeperspring.dto.SankeyDto.SankeyLink;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.mapper.ExpenseMapper;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BudgetFlowServiceTest {

    private static final LocalDate BEGIN = LocalDate.of(2026, 10, 1);
    private static final LocalDate END = LocalDate.of(2026, 10, 31);

    private final Category salary = Category.builder().id(1L).name("Pensja").level(0).build();
    private final Category bonus = Category.builder().id(2L).name("Premia").level(0).build();
    private final Category food = Category.builder().id(3L).name("Jedzenie").level(1).build();
    private final Category flat = Category.builder().id(4L).name("Czynsz").level(1).build();
    private final Category cinema = Category.builder().id(5L).name("Kino").level(2).build();

    private ExpenseRepository expenseRepository;
    private BudgetFlowService service;

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        CategoryLevelService categoryLevelService = mock(CategoryLevelService.class);
        when(categoryLevelService.getCategoryLevels()).thenReturn(Map.of(0, "Wpływy", 1, "Życie", 2, "Rozrywka"));
        ExpenseMapper expenseMapper = mock(ExpenseMapper.class);
        when(expenseMapper.mapToDto(any(Expense.class))).thenAnswer(invocation ->
                ExpenseDTO.builder().note(((Expense) invocation.getArgument(0)).getTitle()).build());
        service = new BudgetFlowService(expenseRepository, categoryLevelService, expenseMapper);
    }

    @Test
    void monthlyFlowGoesFromIncomesThroughLevelsToCategories() {
        when(expenseRepository.findAllByTransactionDateBetween(BEGIN, END)).thenReturn(List.of(
                expense("5000", salary, "pensja"), expense("1000", bonus, "premia"),
                expense("-300", food, "biedronka"), expense("-200", food, "lidl"), expense("-1500", flat, "czynsz"),
                expense("-50", cinema, "kino"), expense("-999", null, "bez kategorii")));

        SankeyDto flow = service.getMonthly(BEGIN, END);

        Set<String> links = flow.getLinks().stream().map(BudgetFlowServiceTest::link).collect(Collectors.toSet());
        assertEquals(Set.of(
                "Pensja (in)->Dochody:5000", "Premia (in)->Dochody:1000",
                "Dochody->Życie:2000", "Dochody->Rozrywka:50",
                "Życie->Jedzenie:500", "Życie->Czynsz:1500",
                // poziom z jedną kategorią rozwija się do pojedynczych wydatków
                "Rozrywka->kino:50"), links);
        Set<String> nodes = flow.getNodes().stream().map(SankeyDto.SankeyNode::getName).collect(Collectors.toSet());
        assertTrue(flow.getLinks().stream().allMatch(l -> nodes.contains(l.getSource()) && nodes.contains(l.getTarget())));
    }

    @Test
    void yearlyFlowStopsAtCategoryLevels() {
        when(expenseRepository.findAllByTransactionDateBetween(BEGIN, END)).thenReturn(List.of(
                expense("5000", salary, "pensja"), expense("-300", food, "biedronka"), expense("-50", cinema, "kino")));

        Set<String> links = service.getYearly(BEGIN, END).getLinks().stream().map(BudgetFlowServiceTest::link).collect(Collectors.toSet());

        assertEquals(Set.of("Pensja (in)->Dochody:5000", "Dochody->Życie:300", "Dochody->Rozrywka:50"), links);
    }

    private static Expense expense(String amount, Category category, String title) {
        return Expense.builder().amount(new BigDecimal(amount)).category(category).title(title).transactionDate(BEGIN).build();
    }

    private static String link(SankeyLink link) {
        return link.getSource() + "->" + link.getTarget() + ":" + link.getValue().stripTrailingZeros().toPlainString();
    }
}
