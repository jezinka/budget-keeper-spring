package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DailyExpensesDTO {

    private Integer day;
    private BigDecimal amount;
}
