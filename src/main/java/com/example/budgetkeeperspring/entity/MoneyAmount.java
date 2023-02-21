package com.example.budgetkeeperspring.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoneyAmount {

    @Id
    private LocalDate date;
    private BigDecimal amount;

    public MoneyAmount(LocalDate date) {
        this.date = date;
    }
}
