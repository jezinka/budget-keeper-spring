package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetPlanDTO {
    private Long id;
    private String category;
    private BigDecimal expense;
    private BigDecimal goal;
    private BigDecimal difference;
    private BigDecimal percentage;

    public BigDecimal getDifference() {
        return expense.subtract(goal);
    }

    public BigDecimal getPercentage() {
        if(goal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        if (getDifference().compareTo(BigDecimal.ZERO) < 0) {
            return getDifference().divide(goal, RoundingMode.CEILING).multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
}
