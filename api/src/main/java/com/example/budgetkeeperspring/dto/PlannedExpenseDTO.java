package com.example.budgetkeeperspring.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlannedExpenseDTO {
    private Integer id;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private Integer planId;

    private String name;
}
