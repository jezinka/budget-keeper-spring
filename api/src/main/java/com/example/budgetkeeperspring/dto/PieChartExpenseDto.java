package com.example.budgetkeeperspring.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@RequiredArgsConstructor
@Builder
public class PieChartExpenseDto {
    private String name;
    private BigDecimal amount;

    public PieChartExpenseDto(String name, BigDecimal amount) {
        this.name = name;
        this.amount = amount.abs().setScale(2, RoundingMode.HALF_UP); // pieChart always positive values
    }
}
