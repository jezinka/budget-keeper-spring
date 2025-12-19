package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YearlyViewDTO {
    private Integer year;
    private List<CategoryLevelSummary> categoryLevels;
    private List<BigDecimal> monthlyIncomeSums; // 12 values (one per month)
    private List<Long> monthlyIncomeTransactionCounts; // 12 values (one per month)
    private BigDecimal totalIncomeYear;
    private List<PieChartData> expensePieData;
    private List<TopExpenseData> topExpenses;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryLevelSummary {
        private Integer level;
        private String name;
        private Map<Integer, BigDecimal> monthlySums; // month (1-12) -> amount
        private Map<Integer, Long> monthlyTransactionCounts; // month (1-12) -> count
        private BigDecimal totalSum;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PieChartData {
        private String name;
        private BigDecimal value;
        private Integer level;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopExpenseData {
        private String name; // truncated description
        private BigDecimal value;
        private String fullDescription;
    }
}
