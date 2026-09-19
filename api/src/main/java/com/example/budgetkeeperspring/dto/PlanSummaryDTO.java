package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class PlanSummaryDTO {
    private PlanDTO plan;
    private List<PlannedExpenseDTO> plannedExpenses;
    private List<ExpenseDTO> plannedExpenseTransactions;
    private List<ExpenseDTO> unplannedExpenses;
    private BigDecimal plannedExpenseAmount;
    private BigDecimal unplannedExpenseAmount;
    private List<PieChartExpenseDto> plannedVsUnplanned;
    private List<CategoryPlanSummaryDTO> categories;
}
