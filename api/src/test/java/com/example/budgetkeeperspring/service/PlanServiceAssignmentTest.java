package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.PlanDTO;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.entity.Plan;
import com.example.budgetkeeperspring.entity.PlannedExpense;
import com.example.budgetkeeperspring.mapper.ExpenseMapper;
import com.example.budgetkeeperspring.mapper.PlanMapper;
import com.example.budgetkeeperspring.mapper.RecurringPlannedExpenseMapper;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import com.example.budgetkeeperspring.repository.PlanRepository;
import com.example.budgetkeeperspring.repository.PlannedExpenseRepository;
import com.example.budgetkeeperspring.repository.RecurringPlannedExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlanServiceAssignmentTest {

    private PlanRepository planRepository;
    private PlannedExpenseRepository plannedExpenseRepository;
    private ExpenseRepository expenseRepository;
    private PlanService service;
    private Plan plan;
    private PlannedExpense rent;

    @BeforeEach
    void setUp() {
        planRepository = mock(PlanRepository.class);
        plannedExpenseRepository = mock(PlannedExpenseRepository.class);
        expenseRepository = mock(ExpenseRepository.class);
        service = new PlanService(planRepository, plannedExpenseRepository, expenseRepository, mock(ExpenseMapper.class),
                mock(PlanMapper.class), mock(RecurringPlannedExpenseMapper.class), mock(RecurringPlannedExpenseRepository.class),
                mock(LogService.class));

        plan = new Plan();
        plan.setId(1);
        plan.setStartDate(LocalDate.of(2026, 10, 1));
        plan.setEndDate(LocalDate.of(2026, 10, 31));
        when(planRepository.findById(1)).thenReturn(Optional.of(plan));

        rent = new PlannedExpense();
        rent.setId(5);
        rent.setPlan(plan);
        rent.setAmount(new BigDecimal("2000.00"));
        when(plannedExpenseRepository.findById(5)).thenReturn(Optional.of(rent));
    }

    @Test
    void assigningExpensesThatCoverTheFullAmountMarksPlannedExpenseAsPaid() {
        Expense first = expense(10L, "-1500.00", LocalDate.of(2026, 10, 2));
        Expense second = expense(11L, "-500", LocalDate.of(2026, 10, 3));
        when(expenseRepository.findAllById(List.of(10L, 11L))).thenReturn(List.of(first, second));

        service.assignExpensesToPlannedExpense(5, List.of(10L, 11L));

        assertTrue(rent.isPaid());
        assertSame(rent, first.getPlannedExpense());
        assertSame(rent, second.getPlannedExpense());
        verify(expenseRepository).saveAll(List.of(first, second));
    }

    @Test
    void partialPaymentLeavesPlannedExpenseUnpaid() {
        Expense partial = expense(10L, "-1500.00", LocalDate.of(2026, 10, 2));
        when(expenseRepository.findAllById(List.of(10L))).thenReturn(List.of(partial));

        service.assignExpenseToPlannedExpense(5, 10L);

        assertFalse(rent.isPaid());
        assertSame(rent, partial.getPlannedExpense());
    }

    @Test
    void alreadyPaidPlannedExpenseStaysPaid() {
        rent.setPaid(true);
        Expense small = expense(10L, "-10.00", LocalDate.of(2026, 10, 2));
        when(expenseRepository.findAllById(List.of(10L))).thenReturn(List.of(small));

        service.assignExpenseToPlannedExpense(5, 10L);

        assertTrue(rent.isPaid());
    }

    @Test
    void rejectsIncomeExpenseOutsidePlanMonthAndAlreadyPlannedExpense() {
        Expense income = expense(10L, "100.00", LocalDate.of(2026, 10, 2));
        Expense september = expense(11L, "-100.00", LocalDate.of(2026, 9, 30));
        Expense planned = expense(12L, "-100.00", LocalDate.of(2026, 10, 2));
        planned.setPlannedExpense(new PlannedExpense());
        when(expenseRepository.findAllById(List.of(10L))).thenReturn(List.of(income));
        when(expenseRepository.findAllById(List.of(11L))).thenReturn(List.of(september));
        when(expenseRepository.findAllById(List.of(12L))).thenReturn(List.of(planned));

        assertEquals(HttpStatus.BAD_REQUEST, status(() -> service.assignExpenseToPlannedExpense(5, 10L)));
        assertEquals(HttpStatus.BAD_REQUEST, status(() -> service.assignExpenseToPlannedExpense(5, 11L)));
        assertEquals(HttpStatus.CONFLICT, status(() -> service.assignExpenseToPlannedExpense(5, 12L)));
        assertFalse(rent.isPaid());
        verify(expenseRepository, never()).saveAll(any());
    }

    @Test
    void rejectsDuplicateExpenseIds() {
        assertEquals(HttpStatus.BAD_REQUEST, status(() -> service.assignExpensesToPlannedExpense(5, List.of(10L, 10L))));
    }

    @Test
    void createsPlannedExpenseFromSelectedExpensesWithSummedAmount() {
        Expense first = expense(10L, "-30.50", LocalDate.of(2026, 10, 2));
        Expense second = expense(11L, "-19.50", LocalDate.of(2026, 10, 31));
        when(expenseRepository.findAllById(List.of(10L, 11L))).thenReturn(List.of(first, second));

        service.createPlannedExpenseFromExpenses(1, List.of(10L, 11L));

        PlannedExpense created = first.getPlannedExpense();
        assertEquals(new BigDecimal("50.00"), created.getAmount());
        assertSame(plan, created.getPlan());
        assertSame(created, second.getPlannedExpense());
    }

    @Test
    void unassignRejectsExpenseFromAnotherPlannedExpense() {
        PlannedExpense other = new PlannedExpense();
        other.setId(6);
        Expense expense = expense(10L, "-10.00", LocalDate.of(2026, 10, 2));
        expense.setPlannedExpense(other);
        when(expenseRepository.findById(10L)).thenReturn(Optional.of(expense));

        assertEquals(HttpStatus.CONFLICT, status(() -> service.unassignExpenseFromPlannedExpense(5, 10L)));

        service.unassignExpenseFromPlannedExpense(6, 10L);
        assertNull(expense.getPlannedExpense());
    }

    @Test
    void cannotCreateSecondPlanForSameMonth() {
        when(planRepository.findByStartDate(LocalDate.of(2026, 10, 1))).thenReturn(Optional.of(plan));
        PlanDTO dto = new PlanDTO();
        dto.setYear(2026);
        dto.setMonth(10);

        assertEquals(HttpStatus.CONFLICT, status(() -> service.create(dto)));
        verify(planRepository, never()).save(any());
    }

    private static Expense expense(Long id, String amount, LocalDate date) {
        return Expense.builder().id(id).amount(new BigDecimal(amount)).transactionDate(date).build();
    }

    private static HttpStatus status(Runnable action) {
        return HttpStatus.valueOf(assertThrows(ResponseStatusException.class, action::run).getStatusCode().value());
    }
}
