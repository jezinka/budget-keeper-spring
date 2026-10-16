package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTrendDTO {

    private String category;
    private Integer level;
    private Map<Integer, BigDecimal> yearly;
    private BigDecimal last12;
    private BigDecimal previous12;
    private BigDecimal diff;
    private Double diffPercent;
    private Double cagr;
}
