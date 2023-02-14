package com.example.budgetkeeperspring.moneyamount;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class MoneyAmount {

    @Id
    private LocalDate date;
    private BigDecimal amount;

    public MoneyAmount(LocalDate date, BigDecimal amount) {
        this.date = date;
        this.amount = amount;
    }
}
