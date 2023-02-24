package com.example.budgetkeeperspring.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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

    @PastOrPresent
    @NotNull
    private LocalDate transactionDate;
    private String title;
    private String payee;

    @NotNull
    private BigDecimal amount;

    private Long categoryId;
    private String categoryName;
    private Boolean deleted;
}
