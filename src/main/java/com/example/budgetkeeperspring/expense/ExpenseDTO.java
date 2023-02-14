package com.example.budgetkeeperspring.expense;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseDTO {
    private Long id;
    private LocalDate transactionDate;
    private String title;
    private String payee;
    private BigDecimal amount;
    private Long categoryId;
    private Long liabilityId;
    private Boolean deleted;

}
