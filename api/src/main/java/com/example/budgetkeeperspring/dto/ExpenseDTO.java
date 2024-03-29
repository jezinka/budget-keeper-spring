package com.example.budgetkeeperspring.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExpenseDTO {
    private Long id;
    private Integer version;

    private String transactionDate;
    private String title;
    private String payee;

    @NotNull
    private BigDecimal amount;

    private Long categoryId;
    private String categoryName;
    private Boolean deleted;

    private String note;
}
