package com.example.budgetkeeperspring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;
    private Integer version;

    @NotNull
    @NotBlank
    private String name;

    @Builder.Default
    private boolean useInYearlyCharts = Boolean.TRUE;
}
