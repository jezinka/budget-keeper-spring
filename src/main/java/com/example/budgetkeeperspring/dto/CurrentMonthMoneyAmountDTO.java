package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentMonthMoneyAmountDTO {

    private BigDecimal start;
    private BigDecimal incomes;
    private BigDecimal expenses;
    private BigDecimal accountBalance;

    public CurrentMonthMoneyAmountDTO(BigDecimal start, BigDecimal incomes, BigDecimal expenses) {
        this.start = start;
        this.incomes = incomes;
        this.expenses = expenses;
        this.accountBalance = start.add(incomes).add(expenses);
    }
}
