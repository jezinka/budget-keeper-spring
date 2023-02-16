package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentMonthMoneyAmountDTO {

    private BigDecimal start;
    private BigDecimal incomes;
    private BigDecimal expenses;
    private BigDecimal accountBalance;

    public BigDecimal getAccountBalance() {
        return start.add(incomes).add(expenses);
    }

    public CurrentMonthMoneyAmountDTO(BigDecimal start, BigDecimal incomes, BigDecimal expenses) {
        this.start = start;
        this.incomes = incomes;
        this.expenses = expenses;
    }
}
