package com.example.budgetkeeperspring.moneyAmount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentMonthMoneyAmount {

    private BigDecimal start;
    private BigDecimal incomes;
    private BigDecimal expenses;
    private BigDecimal accountBalance;

    public BigDecimal getAccountBalance() {
        return start.add(incomes).add(expenses);
    }

    CurrentMonthMoneyAmount(BigDecimal start, BigDecimal incomes, BigDecimal expenses) {
        this.start = start;
        this.incomes = incomes;
        this.expenses = expenses;
    }
}
