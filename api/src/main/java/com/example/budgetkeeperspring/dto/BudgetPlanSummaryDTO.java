package com.example.budgetkeeperspring.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetPlanSummaryDTO {
    private BigDecimal investments;
    private BigDecimal noPayGoal;
}
