package com.example.budgetkeeperspring.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class LiabilityLookoutDTO {
    private Long id;
    private Integer version;
    private LocalDate date;
    private BigDecimal outcome;

    private Long liabilityId;
    private String liabilityName;

    private Long bankId;
    private String bankName;
}
