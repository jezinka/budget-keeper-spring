package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.entity.Plan;
import com.example.budgetkeeperspring.entity.PlannedExpense;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class PlanRepositoryTest {

    @Autowired
    PlanRepository planRepository;

    @Autowired
    PlannedExpenseRepository plannedExpenseRepository;

    @Autowired
    ExpenseRepository expenseRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void aggregatesAdminSummariesInTheDatabase() {
        Plan plan = new Plan();
        plan.setStartDate(LocalDate.of(2026, 9, 1));
        plan.setEndDate(LocalDate.of(2026, 9, 30));
        plan = planRepository.save(plan);
        Category expenses = categoryRepository.save(new Category("Expenses"));
        Category investments = categoryRepository.save(new Category("Investments"));
        investments.setLevel(2);

        PlannedExpense unpaid = plannedExpense(plan, "Unpaid", "100.00", false);
        PlannedExpense paid = plannedExpense(plan, "Paid", "70.00", true);
        expenseRepository.saveAll(List.of(
                expense("-40.00", LocalDate.of(2026, 9, 5), expenses, unpaid, false),
                expense("-10.00", LocalDate.of(2026, 9, 6), expenses, unpaid, true),
                expense("-5.00", LocalDate.of(2026, 9, 7), investments, unpaid, false),
                expense("-9.00", LocalDate.of(2026, 9, 7), null, unpaid, false),
                expense("-30.00", LocalDate.of(2026, 9, 8), expenses, paid, false),
                expense("-20.00", LocalDate.of(2026, 9, 9), expenses, null, false),
                expense("5.00", LocalDate.of(2026, 9, 10), expenses, null, false),
                expense("-12.00", LocalDate.of(2026, 9, 11), expenses, null, true),
                expense("-3.00", LocalDate.of(2026, 9, 12), investments, null, false),
                expense("-8.00", LocalDate.of(2026, 9, 13), null, null, false)
        ));

        Object[] summary = planRepository.findAllAdminSummaries(2).get(0);

        assertEquals(plan.getId(), ((Number) summary[0]).intValue());
        assertEquals(2026, ((Number) summary[1]).intValue());
        assertEquals(9, ((Number) summary[2]).intValue());
        assertEquals(0, new BigDecimal("70.00").compareTo((BigDecimal) summary[3]));
        assertEquals(0, new BigDecimal("20.00").compareTo((BigDecimal) summary[4]));
        assertEquals(0, new BigDecimal("30.00").compareTo((BigDecimal) summary[5]));
        assertEquals(0, new BigDecimal("60.00").compareTo((BigDecimal) summary[6]));
        assertEquals(2, ((Number) summary[7]).longValue());
        assertEquals(3, ((Number) summary[8]).longValue());
    }

    private PlannedExpense plannedExpense(Plan plan, String name, String amount, boolean paid) {
        PlannedExpense plannedExpense = new PlannedExpense();
        plannedExpense.setPlan(plan);
        plannedExpense.setName(name);
        plannedExpense.setAmount(new BigDecimal(amount));
        plannedExpense.setPaid(paid);
        return plannedExpenseRepository.save(plannedExpense);
    }

    private Expense expense(String amount, LocalDate date, Category category, PlannedExpense plannedExpense, boolean excluded) {
        return Expense.builder()
                .amount(new BigDecimal(amount))
                .transactionDate(date)
                .category(category)
                .plannedExpense(plannedExpense)
                .excludedFromPlan(excluded)
                .build();
    }
}
