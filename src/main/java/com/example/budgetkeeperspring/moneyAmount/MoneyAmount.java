package com.example.budgetkeeperspring.moneyAmount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyAmount {

    Float amount;
    Float income;
    Float outcome;
    Float accountBalance;
}
