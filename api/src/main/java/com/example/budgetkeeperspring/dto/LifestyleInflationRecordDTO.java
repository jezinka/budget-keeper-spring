package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LifestyleInflationRecordDTO {
    private String date;
    private BigDecimal expensesSum;
    private String category;
    private String categoryLevel;
}
