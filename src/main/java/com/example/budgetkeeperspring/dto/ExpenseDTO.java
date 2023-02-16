package com.example.budgetkeeperspring.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class ExpenseDTO {
    private Long id;
    private LocalDate transactionDate;
    private String title;
    private String payee;
    private BigDecimal amount;
    private Long categoryId;
    private String categoryName;
    private Boolean deleted;
}
