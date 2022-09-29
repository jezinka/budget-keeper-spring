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

    private Date transactionDate;
    private String title;
    private String payee;
    private Float amount;
    private String category;
    private Long categoryId;
}
