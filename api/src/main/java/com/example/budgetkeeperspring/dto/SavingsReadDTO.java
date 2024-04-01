package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SavingsReadDTO {
    private Long id;

    private BigDecimal amount;
    private LocalDate date;

    private Long savingsId;
    private String name;
    private String fundGroup;
    private String purpose;
    private String risk;
    private String autoType;

    private String groupName;

}
