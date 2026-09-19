package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategoryPlanSummaryDTO {
    private Long categoryId;
    private String categoryName;
    private BigDecimal plannedAmount;
    private BigDecimal plannedExpenseAmount;
    private BigDecimal unplannedExpenseAmount;

    public BigDecimal getActualAmount() {
        return plannedExpenseAmount.add(unplannedExpenseAmount);
    }

    public boolean isUnderPlanned() {
        return getActualAmount().compareTo(plannedAmount) > 0;
    }

    public BigDecimal getDifference() {
        return plannedAmount.subtract(getActualAmount());
    }
}
