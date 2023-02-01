package com.example.budgetkeeperspring.groupedExpenses;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity
public class ExpensesCategory {

    @Id
    Long id;
    Date transactionDate;
    String title;
    String payee;
    Long amount;
    String category;
    Long categoryId;
    Long liabilityId;
    Boolean useInYearlyCharts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCategory() {
        if (category == null) {
            return "";
        }

        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getLiabilityId() {
        return liabilityId;
    }

    public void setLiabilityId(Long liabilityId) {
        this.liabilityId = liabilityId;
    }

    public Boolean getUseInYearlyCharts() {
        return useInYearlyCharts;
    }

    public void setUseInYearlyCharts(Boolean useInYearlyCharts) {
        this.useInYearlyCharts = useInYearlyCharts;
    }

    public int getTransactionMonth() {
        return transactionDate.getMonth() + 1;
    }
}
