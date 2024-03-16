package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetPlanDTO {
    private String category;
    private BigDecimal currentMonthSum;
    private BigDecimal previousYearMean;
    private BigDecimal goal;

    public BudgetPlanDTO(String category, BigDecimal previousYearMean, BigDecimal hipGoal) {
        this.category = category;
        this.previousYearMean = previousYearMean;
        this.goal = hipGoal;
    }
}
