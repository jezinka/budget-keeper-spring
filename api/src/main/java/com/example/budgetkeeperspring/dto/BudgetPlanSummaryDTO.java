package com.example.budgetkeeperspring.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetPlanSummaryDTO {
    private BigDecimal sumPlanned; // wydane
    private BigDecimal noBuy;
    private BigDecimal otherExpenses;
    private BigDecimal total;
    private BigDecimal sumGoal; // zaplanowane
    private BigDecimal overGoalDifference;
}
