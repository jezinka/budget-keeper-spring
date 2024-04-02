package com.example.budgetkeeperspring.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class SavingsReadGroupedDTO {
    private Long savingsId;
    private String name;
    private List<SavingsChartDataDTO> data = new ArrayList<>();

    public SavingsReadGroupedDTO(SavingsReadDTO savingsReadDTO) {
        this.savingsId = savingsReadDTO.getSavingsId();
        this.name = savingsReadDTO.getGroupName();
        this.data.add(new SavingsChartDataDTO(savingsReadDTO));
    }

    @Data
    public static class SavingsChartDataDTO {
        private Long date;
        private BigDecimal amount;

        public SavingsChartDataDTO(SavingsReadDTO savingsReadDTO) {
            this.amount = savingsReadDTO.getAmount();
            this.date = Timestamp.valueOf(savingsReadDTO.getDate().atStartOfDay()).getTime();
        }
    }
}
