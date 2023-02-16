package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthCategoryAmountDTO {

    private Integer month;
    private String category;
    private BigDecimal amount;
}
