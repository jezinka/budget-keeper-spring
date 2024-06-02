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
public class BudgetPlanSummaryDTO {
    private BigDecimal sumPlanned;
    private BigDecimal noBuy;
    private BigDecimal otherExpenses;
    private BigDecimal total;
}
