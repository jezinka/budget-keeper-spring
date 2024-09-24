package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MonthCategoryAmountDTO {

    private Integer month;
    private String category;
    private BigDecimal amount;
    private BigDecimal goalAmount;
    private long transactionCount;

    public MonthCategoryAmountDTO(Integer month, String category, BigDecimal amount) {
        this.month = month;
        this.category = category;
        this.amount = amount;
    }

    public MonthCategoryAmountDTO(Integer month, String category, BigDecimal amount, long transactionCount) {
        this(month, category, amount);
        this.transactionCount = transactionCount;
    }
}
