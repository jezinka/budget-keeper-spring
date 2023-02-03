package com.example.budgetkeeperspring.moneyAmount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentMonthMoneyAmount {

    private float start;
    private float incomes;
    private float expenses;
    private float accountBalance;

    public float getAccountBalance() {
        return start + incomes + expenses;
    }

    CurrentMonthMoneyAmount(float start, float incomes, float expenses) {
        this.start = start;
        this.incomes = incomes;
        this.expenses = expenses;
    }
}
