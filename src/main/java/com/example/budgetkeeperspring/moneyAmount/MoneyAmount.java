package com.example.budgetkeeperspring.moneyAmount;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity
@Data
@NoArgsConstructor
public class MoneyAmount {

    @Id
    private Date date;
    private Float amount;

    public MoneyAmount(Date date, Float amount) {
        this.date = date;
        this.amount = amount;
    }
}
