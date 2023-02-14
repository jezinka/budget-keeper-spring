package com.example.budgetkeeperspring.expense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthCategoryAmount {

    private Integer month;
    private String category;
    private BigDecimal amount;
}
