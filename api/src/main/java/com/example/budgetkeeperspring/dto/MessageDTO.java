package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MessageDTO {

    private LocalDate receivedAt;
    private String title;
    private String payee;
    private BigDecimal amount;

    public String getTitle() {
        if (title == null) {
            title = "";
        }
        return title;
    }

    public String getPayee() {
        if (payee == null) {
            payee = "";
        }
        return payee;
    }
}
