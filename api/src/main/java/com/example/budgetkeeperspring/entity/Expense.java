package com.example.budgetkeeperspring.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;

@SQLDelete(sql = "UPDATE expense SET deleted = true WHERE id=? and version = ?")
@Where(clause = "deleted=false")
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Version
    private Integer version;

    @PastOrPresent
    @NotNull
    private LocalDate transactionDate;

    @Size(max = 200)
    @Column(length = 200)
    private String title;
    private String payee;

    @NotNull
    @Digits(integer = 5, fraction = 2)
    private BigDecimal amount;

    @ManyToOne()
    @Fetch(FetchMode.JOIN)
    private Category category;

    @Builder.Default
    private boolean deleted = Boolean.FALSE;

    @Builder.Default
    private Boolean manually = Boolean.FALSE;

    private String note;

    public String getCategoryName() {
        if (category != null) {
            return category.getName();
        }
        return StringUtils.EMPTY;
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
