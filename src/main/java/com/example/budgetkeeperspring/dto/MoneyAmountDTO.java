package com.example.budgetkeeperspring.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoneyAmountDTO {

    private Long id;
    private Integer version;

    @NotNull
    private LocalDate date;

    @NotNull
    @Digits(integer = 5, fraction = 2)
    private BigDecimal amount;

    @CreationTimestamp
    private LocalDate createdAt;
}
