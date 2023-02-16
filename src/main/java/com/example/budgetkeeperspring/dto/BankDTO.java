package com.example.budgetkeeperspring.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class BankDTO {
    Long id;
    String name;
    Set<LiabilityDTO> liabilities;
}
