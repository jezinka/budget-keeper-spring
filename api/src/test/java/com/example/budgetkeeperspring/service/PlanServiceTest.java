package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.PlanDTO;
import com.example.budgetkeeperspring.dto.PlanSummaryDTO;
import com.example.budgetkeeperspring.dto.PlannedExpenseDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.entity.Plan;
import com.example.budgetkeeperspring.entity.PlannedExpense;
import com.example.budgetkeeperspring.entity.RecurringPlannedExpense;
import com.example.budgetkeeperspring.mapper.ExpenseMapper;
import com.example.budgetkeeperspring.mapper.PlanMapper;
import com.example.budgetkeeperspring.mapper.RecurringPlannedExpenseMapper;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import com.example.budgetkeeperspring.repository.PlanRepository;
import com.example.budgetkeeperspring.repository.PlannedExpenseRepository;
import com.example.budgetkeeperspring.repository.RecurringPlannedExpenseRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlanServiceTest {

    @Test
    void summarySeparatesPlannedAndUnplannedExpensesAndFlagsShortCategoryPlan() {
        PlanRepository planRepository = mock(PlanRepository.class);
        PlannedExpenseRepository plannedExpenseRepository = mock(PlannedExpenseRepository.class);
        ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
        ExpenseMapper expenseMapper = mock(ExpenseMapper.class);
        PlanMapper planMapper = mock(PlanMapper.class);
        RecurringPlannedExpenseMapper recurringMapper = mock(RecurringPlannedExpenseMapper.class);
        PlanService service = new PlanService(planRepository, plannedExpenseRepository, expenseRepository, expenseMapper, planMapper, recurringMapper, mock(RecurringPlannedExpenseRepository.class));

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
        Expense planned = expense(new BigDecimal("-120.00"), category);
        planned.setPlannedExpense(budget);
        Expense unplanned = expense(new BigDecimal("-50.00"), category);

        when(planRepository.findByStartDate(plan.getStartDate())).thenReturn(Optional.of(plan));
        when(plannedExpenseRepository.findAllByPlanId(1)).thenReturn(List.of(budget));
        when(expenseRepository.findAllPlannedByTransactionDateBetweenAndPlanId(plan.getStartDate(), plan.getEndDate(), 1)).thenReturn(List.of(planned));
        when(expenseRepository.findAllUnplannedByTransactionDateBetween(plan.getStartDate(), plan.getEndDate())).thenReturn(List.of(unplanned));
        when(expenseMapper.mapToDto(any())).thenReturn(null);

        PlanSummaryDTO summary = service.summary(2026, 9);

        assertEquals(new BigDecimal("120.00"), summary.getPlannedExpenseAmount());
        assertEquals(new BigDecimal("50.00"), summary.getUnplannedExpenseAmount());
        assertEquals(BigDecimal.ZERO, summary.getPaidPlannedAmount());
        assertEquals(BigDecimal.ZERO, summary.getRemainingPlannedAmount());
        assertEquals(1, summary.getRemainingPlannedCount());
        assertEquals(new BigDecimal("170.00"), summary.getCategories().get(0).getActualAmount());
        assertTrue(summary.getCategories().get(0).isUnderPlanned());
        assertEquals(new BigDecimal("100.00"), summary.getPlannedVsUnplanned().get(0).getAmount());
        assertEquals(new BigDecimal("20.00"), summary.getPlannedVsUnplanned().get(1).getAmount());
        assertEquals(new BigDecimal("50.00"), summary.getPlannedVsUnplanned().get(2).getAmount());

        unplanned.setExcludedFromPlan(true);
        assertEquals(BigDecimal.ZERO, service.summary(2026, 9).getUnplannedExpenseAmount());

        budget.setPaid(true);
        assertEquals(new BigDecimal("120.00"), service.summary(2026, 9).getPaidPlannedAmount());
    }

    @Test
    void manuallyMarksPlannedExpenseAsPaid() {
        PlanRepository planRepository = mock(PlanRepository.class);
        PlannedExpenseRepository plannedExpenseRepository = mock(PlannedExpenseRepository.class);
        Plan plan = new Plan();
        plan.setId(1);
        PlannedExpense plannedExpense = new PlannedExpense();
        plannedExpense.setId(3);
        plannedExpense.setPlan(plan);
        plannedExpense.setAmount(new BigDecimal("39.00"));
        when(plannedExpenseRepository.findById(3)).thenReturn(Optional.of(plannedExpense));
        when(plannedExpenseRepository.save(plannedExpense)).thenReturn(plannedExpense);

        PlanMapper planMapper = mock(PlanMapper.class);
        when(planMapper.mapToDto(plannedExpense)).thenAnswer(invocation -> {
            PlannedExpense expense = invocation.getArgument(0);
            PlannedExpenseDTO dto = new PlannedExpenseDTO();
            dto.setPaid(expense.isPaid());
            return dto;
        });

        PlanService service = new PlanService(planRepository, plannedExpenseRepository, mock(ExpenseRepository.class), mock(ExpenseMapper.class), planMapper, mock(RecurringPlannedExpenseMapper.class), mock(RecurringPlannedExpenseRepository.class));

        assertTrue(service.updatePaid(3, true).getPaid());
    }

    @Test
    void createsAnUnpaidRecurringPaymentForNewPlan() {
        PlanRepository planRepository = mock(PlanRepository.class);
        PlannedExpenseRepository plannedExpenseRepository = mock(PlannedExpenseRepository.class);
        RecurringPlannedExpenseRepository recurringRepository = mock(RecurringPlannedExpenseRepository.class);
        RecurringPlannedExpense recurringPayment = new RecurringPlannedExpense();
        recurringPayment.setId(2);
        recurringPayment.setName("Disney");
        recurringPayment.setAmount(new BigDecimal("39.00"));
        recurringPayment.setDueDay(31);

        when(planRepository.findByStartDate(LocalDate.of(2026, 2, 1))).thenReturn(Optional.empty());
        when(planRepository.save(any())).thenAnswer(invocation -> {
            Plan plan = invocation.getArgument(0);
            plan.setId(1);
            return plan;
        });
        when(recurringRepository.findAllByActiveTrueOrderByDueDayAscNameAsc()).thenReturn(List.of(recurringPayment));

        PlanMapper planMapper = mock(PlanMapper.class);
        doAnswer(invocation -> {
            PlanDTO source = invocation.getArgument(0);
            Plan target = invocation.getArgument(1);
            YearMonth month = YearMonth.of(source.getYear(), source.getMonth());
            target.setStartDate(month.atDay(1));
            target.setEndDate(month.atEndOfMonth());
            return null;
        }).when(planMapper).updatePlanFromDto(any(PlanDTO.class), any(Plan.class));

        RecurringPlannedExpenseMapper recurringMapper = mock(RecurringPlannedExpenseMapper.class);
        when(recurringMapper.mapToPlannedExpense(any(Plan.class), any(RecurringPlannedExpense.class))).thenAnswer(invocation -> {
            Plan targetPlan = invocation.getArgument(0);
            RecurringPlannedExpense source = invocation.getArgument(1);
            PlannedExpense payment = new PlannedExpense();
            payment.setPlan(targetPlan);
            payment.setRecurringPayment(source);
            payment.setName(source.getName());
            payment.setAmount(source.getAmount());
            payment.setDueDate(targetPlan.getStartDate().withDayOfMonth(Math.min(source.getDueDay(), targetPlan.getEndDate().getDayOfMonth())));
            return payment;
        });

        PlanService service = new PlanService(planRepository, plannedExpenseRepository, mock(ExpenseRepository.class), mock(ExpenseMapper.class), planMapper, recurringMapper, recurringRepository);
        PlanDTO dto = new PlanDTO();
        dto.setYear(2026);
        dto.setMonth(2);

        service.create(dto);

        ArgumentCaptor<Iterable<PlannedExpense>> payments = ArgumentCaptor.forClass(Iterable.class);
        verify(plannedExpenseRepository).saveAll(payments.capture());
        PlannedExpense payment = payments.getValue().iterator().next();
        assertEquals("Disney", payment.getName());
        assertEquals(LocalDate.of(2026, 2, 28), payment.getDueDate());
        assertFalse(payment.isPaid());
    }

    @Test
    void convertsPlannedExpenseToRecurringOnlyOnce() {
        PlanRepository planRepository = mock(PlanRepository.class);
        PlannedExpenseRepository plannedExpenseRepository = mock(PlannedExpenseRepository.class);
        RecurringPlannedExpenseRepository recurringRepository = mock(RecurringPlannedExpenseRepository.class);
        Plan plan = new Plan();
        plan.setId(1);
        plan.setStartDate(LocalDate.of(2026, 9, 1));
        PlannedExpense plannedExpense = new PlannedExpense();
        plannedExpense.setId(3);
        plannedExpense.setPlan(plan);
        plannedExpense.setName("Spotify");
        plannedExpense.setAmount(new BigDecimal("29.00"));
        plannedExpense.setDueDate(LocalDate.of(2026, 9, 10));
        when(plannedExpenseRepository.findById(3)).thenReturn(Optional.of(plannedExpense));
        when(plannedExpenseRepository.save(plannedExpense)).thenReturn(plannedExpense);
        when(recurringRepository.save(any())).thenAnswer(invocation -> {
            RecurringPlannedExpense payment = invocation.getArgument(0);
            payment.setId(4);
            return payment;
        });
        PlanService service = new PlanService(planRepository, plannedExpenseRepository, mock(ExpenseRepository.class), mock(ExpenseMapper.class), mock(PlanMapper.class), mock(RecurringPlannedExpenseMapper.class), recurringRepository);

        service.convertPlannedExpenseToRecurring(3);
        service.convertPlannedExpenseToRecurring(3);

        verify(recurringRepository, times(1)).save(any());
        assertEquals(4, plannedExpense.getRecurringPayment().getId());
    }

    private Expense expense(BigDecimal amount, Category category) {
        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setCategory(category);
        return expense;
    }
}
