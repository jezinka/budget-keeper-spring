package com.example.budgetkeeperspring.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class LiabilityLookoutDTO {
    Long id;
    LocalDate date;
    BigDecimal outcome;

    Long liabilityId;
    String liabilityName;

    Long bankId;
    String bankName;
}
