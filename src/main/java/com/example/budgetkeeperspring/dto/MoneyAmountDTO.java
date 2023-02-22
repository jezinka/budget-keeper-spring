package com.example.budgetkeeperspring.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class MoneyAmountDTO {

    private Long id;
    private Integer version;
    private LocalDate date;
    private BigDecimal amount;
    private LocalDate createdAt;
}
