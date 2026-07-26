package com.example.budgetkeeperspring.dto;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreeMapExpenseDto {
    private String name;
    private List<TreeMapExpenseDto> children;
    private BigDecimal amount;

    public TreeMapExpenseDto(String name, List<TreeMapExpenseDto> children) {
        this.name = name;
        this.children = children;
    }

    public TreeMapExpenseDto(String name, BigDecimal amount) {
        this.name = name;
        this.amount = amount.abs().setScale(2, RoundingMode.HALF_UP);
    }
}
