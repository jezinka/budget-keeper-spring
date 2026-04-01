package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseInfoDTO {
    private String price;
    private String name;
    private String orderDate;
    private String sendDate;
}
