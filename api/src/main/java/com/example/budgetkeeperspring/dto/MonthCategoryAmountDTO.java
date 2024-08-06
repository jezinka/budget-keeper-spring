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
    private Boolean goalSucceeded;

    public MonthCategoryAmountDTO(Integer month, String category, BigDecimal amount) {
        this.month = month;
        this.category = category;
        this.amount = amount;
    }
}
