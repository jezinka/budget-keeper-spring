package com.example.budgetkeeperspring.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDate;

@SQLDelete(sql = "UPDATE expense SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Integer version;

    private LocalDate transactionDate;
    private String title;
    private String payee;
    private BigDecimal amount;

    @ManyToOne()
    @Fetch(FetchMode.JOIN)
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
