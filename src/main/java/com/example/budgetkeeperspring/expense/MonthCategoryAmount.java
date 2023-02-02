package com.example.budgetkeeperspring.expense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthCategoryAmount {

    private Integer month;
    private String category;
    private Float amount;
}
