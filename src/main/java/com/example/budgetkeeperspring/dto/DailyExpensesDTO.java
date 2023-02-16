package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyExpensesDTO {

    private Integer day;
    private BigDecimal amount;
}
