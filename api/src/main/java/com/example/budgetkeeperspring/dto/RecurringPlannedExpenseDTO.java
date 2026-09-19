package com.example.budgetkeeperspring.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecurringPlannedExpenseDTO {
    private Integer id;

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    @Min(1)
    @Max(31)
    private Integer dueDay;

    private Boolean active;
}
