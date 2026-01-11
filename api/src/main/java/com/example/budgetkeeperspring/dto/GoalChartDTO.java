package com.example.budgetkeeperspring.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class GoalChartDTO {
    private BigDecimal target;
    private BigDecimal actual;
}
