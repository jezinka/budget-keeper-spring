package com.example.budgetkeeperspring.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlanDTO {
    private Integer id;

    @NotNull
    @Min(1)
    private Integer year;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer month;
}
