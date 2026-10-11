package com.example.budgetkeeperspring.dto;

import java.math.BigDecimal;

public record PlanAdminSummaryDTO(
        Integer id,
        int year,
        int month,
        BigDecimal plannedExpenseAmount,
        BigDecimal unplannedExpenseAmount,
        BigDecimal paidPlannedAmount,
        BigDecimal remainingPlannedAmount,
        long plannedCount,
        long unplannedCount) {
}
