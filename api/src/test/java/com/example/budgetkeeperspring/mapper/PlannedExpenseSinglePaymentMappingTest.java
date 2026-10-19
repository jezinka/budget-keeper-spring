package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.PlannedExpenseDTO;
import com.example.budgetkeeperspring.entity.Plan;
import com.example.budgetkeeperspring.entity.PlannedExpense;
import com.example.budgetkeeperspring.entity.RecurringPlannedExpense;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlannedExpenseSinglePaymentMappingTest {

    @Test
    void recurringPaymentPassesSinglePaymentToNewMonth() {
        Plan plan = new Plan();
        plan.setStartDate(LocalDate.of(2026, 11, 1));
        plan.setEndDate(LocalDate.of(2026, 11, 30));
        RecurringPlannedExpense envelope = new RecurringPlannedExpense();
        envelope.setName("Jedzenie na mieście");
        envelope.setAmount(new BigDecimal("400.00"));
        envelope.setDueDay(1);
        envelope.setSinglePayment(false);

        assertFalse(new RecurringPlannedExpenseMapperImpl().mapToPlannedExpense(plan, envelope).isSinglePayment());
    }

    @Test
    void updateKeepsSinglePaymentWhenDtoOmitsIt() {
        PlanMapper mapper = new PlanMapperImpl();
        PlannedExpense plannedExpense = new PlannedExpense();
        PlannedExpenseDTO dto = new PlannedExpenseDTO();
        dto.setAmount(new BigDecimal("400.00"));

        mapper.updatePlannedExpenseFromDto(dto, new Plan(), plannedExpense);
        assertTrue(plannedExpense.isSinglePayment());

        dto.setSinglePayment(false);
        mapper.updatePlannedExpenseFromDto(dto, new Plan(), plannedExpense);
        assertFalse(plannedExpense.isSinglePayment());
        assertFalse(mapper.mapToDto(plannedExpense).getSinglePayment());
    }
}
