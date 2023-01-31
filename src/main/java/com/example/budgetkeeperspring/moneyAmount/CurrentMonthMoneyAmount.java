package com.example.budgetkeeperspring.moneyAmount;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CurrentMonthMoneyAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Float amount;

    private Float income;
    private Float expenses;
    private Float accountBalance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getIncome() {
        return income;
    }

    public void setIncome(Float income) {
        this.income = income;
    }

    public Float getExpenses() {
        return expenses;
    }

    public void setExpenses(Float expenses) {
        this.expenses = expenses;
    }

    public Float getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Float accountBalance) {
        this.accountBalance = accountBalance;
    }
}
