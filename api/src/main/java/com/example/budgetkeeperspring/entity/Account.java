package com.example.budgetkeeperspring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Size(max = 50)
    @Column(length = 50)
    private String name;

    @Size(max = 26)
    @Column(length = 26)
    private String accountNumber;

    private String note;

    @Column(precision = 7, scale = 2)
    private BigDecimal currentAmount;

    @Builder.Default
    private Boolean sinkingFund = Boolean.FALSE;

    @Builder.Default
    private Boolean defaultAccount = Boolean.FALSE;
}
