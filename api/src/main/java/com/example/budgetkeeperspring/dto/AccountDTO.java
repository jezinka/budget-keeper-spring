package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long id;
    private String name;
    private String accountNumber;
    private String note;
    private BigDecimal currentAmount;
    private Boolean sinkingFund;
    private Boolean defaultAccount;
}
