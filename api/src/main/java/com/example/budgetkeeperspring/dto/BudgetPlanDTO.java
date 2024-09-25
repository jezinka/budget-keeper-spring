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
    private Long id;
    private String category;
    private BigDecimal expense;
    private BigDecimal goal;
    private BigDecimal difference;
    private int transactionCount;

    public BigDecimal getDifference() {
        return expense.subtract(goal);
    }
}
