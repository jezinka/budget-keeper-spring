package com.example.budgetkeeperspring.entity;

import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@SQLDelete(sql = "UPDATE expense SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate transactionDate;
    private String title;
    private String payee;
    private BigDecimal amount;

    @ManyToOne()
    private Category category;

    private Boolean deleted = false;

    public String getCategoryName() {
        if (category != null) {
            return category.getName();
        }
        return "";
    }

    public int getTransactionDay() {
        if (transactionDate != null) {
            return transactionDate.getDayOfMonth();
        }
        return -1;
    }

    public int getTransactionMonth() {
        if (transactionDate != null) {
            return transactionDate.getMonthValue();
        }
        return -1;
    }

    public int getTransactionYear() {
        if (transactionDate != null) {
            return transactionDate.getYear();
        }
        return -1;
    }
}