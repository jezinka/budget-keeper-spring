package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExpenseDTO {
    private Long id;
    private Integer version;
    private LocalDate transactionDate;
    private String title;
    private String payee;
    private BigDecimal amount;
    private Long categoryId;
    private String categoryName;
    private Boolean deleted;
}
