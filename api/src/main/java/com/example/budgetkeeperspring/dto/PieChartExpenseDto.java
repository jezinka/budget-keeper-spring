package com.example.budgetkeeperspring.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@Builder
public class PieChartExpenseDto {
    private String name;
    private BigDecimal amount;

    public PieChartExpenseDto(String name, BigDecimal amount) {
        this.name = name;
        this.amount = amount.abs(); // pieChart always positive values
    }
}
