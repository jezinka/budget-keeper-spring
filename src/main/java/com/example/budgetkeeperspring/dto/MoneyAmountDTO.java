package com.example.budgetkeeperspring.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class MoneyAmountDTO {

    private Long id;
    private Integer version;

    @NotNull
    @PastOrPresent
    private LocalDate date;

    @NotNull
    @Digits(integer = 5, fraction = 2)
    private BigDecimal amount;
    private LocalDate createdAt;
}
