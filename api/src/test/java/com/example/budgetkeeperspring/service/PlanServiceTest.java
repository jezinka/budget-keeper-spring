package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.PlanSummaryDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.entity.Plan;
import com.example.budgetkeeperspring.entity.PlannedExpense;
import com.example.budgetkeeperspring.mapper.ExpenseMapper;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import com.example.budgetkeeperspring.repository.PlanRepository;
import com.example.budgetkeeperspring.repository.PlannedExpenseRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PlanServiceTest {

    @Test
    void summarySeparatesPlannedAndUnplannedExpensesAndFlagsShortCategoryPlan() {
        PlanRepository planRepository = mock(PlanRepository.class);
        PlannedExpenseRepository plannedExpenseRepository = mock(PlannedExpenseRepository.class);
        ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
        ExpenseMapper expenseMapper = mock(ExpenseMapper.class);
        PlanService service = new PlanService(planRepository, plannedExpenseRepository, expenseRepository, expenseMapper);

        Plan plan = new Plan();
        plan.setId(1);
        plan.setStartDate(LocalDate.of(2026, 9, 1));
        plan.setEndDate(LocalDate.of(2026, 9, 30));
        Category category = new Category("Food");
        category.setId(2L);
        PlannedExpense budget = new PlannedExpense();
        budget.setId(3);
        budget.setPlan(plan);
        budget.setAmount(new BigDecimal("100.00"));
        Expense planned = expense(new BigDecimal("-70.00"), category);
        planned.setPlannedExpense(budget);
        Expense unplanned = expense(new BigDecimal("-50.00"), category);

        when(planRepository.findByStartDate(plan.getStartDate())).thenReturn(Optional.of(plan));
        when(plannedExpenseRepository.findAllByPlanId(1)).thenReturn(List.of(budget));
        when(expenseRepository.findAllPlannedByTransactionDateBetweenAndPlanId(plan.getStartDate(), plan.getEndDate(), 1)).thenReturn(List.of(planned));
        when(expenseRepository.findAllUnplannedByTransactionDateBetween(plan.getStartDate(), plan.getEndDate())).thenReturn(List.of(unplanned));
        when(expenseMapper.mapToDto(any())).thenReturn(null);

        PlanSummaryDTO summary = service.summary(2026, 9);

        assertEquals(new BigDecimal("70.00"), summary.getPlannedExpenseAmount());
        assertEquals(new BigDecimal("50.00"), summary.getUnplannedExpenseAmount());
        assertEquals(new BigDecimal("120.00"), summary.getCategories().get(0).getActualAmount());
        assertTrue(summary.getCategories().get(0).isUnderPlanned());
    }

    private Expense expense(BigDecimal amount, Category category) {
        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setCategory(category);
        return expense;
    }
}
