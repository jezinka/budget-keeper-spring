package com.example.budgetkeeperspring.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private Long id;

    Date transactionDate;
    String title;
    String payee;
    Float amount;
    String category;
}
