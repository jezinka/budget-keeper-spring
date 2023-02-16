package com.example.budgetkeeperspring.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class LiabilityDTO {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private BankDTO bank;
    private Set<LiabilityLookoutDTO> liabilityLookouts;
}
