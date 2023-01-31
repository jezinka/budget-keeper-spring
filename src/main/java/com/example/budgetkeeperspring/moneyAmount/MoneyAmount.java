package com.example.budgetkeeperspring.moneyAmount;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class MoneyAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate month;
    private Float amount;

    public MoneyAmount(LocalDate month, Float amount) {
        this.month = month;
        this.amount = amount;
    }
}
