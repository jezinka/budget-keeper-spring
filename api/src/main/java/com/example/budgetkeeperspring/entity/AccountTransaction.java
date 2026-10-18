package com.example.budgetkeeperspring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction imported from an external bank statement (e.g. Revolut).
 * Deliberately separate from {@link Expense}, so it does not affect the main budget views.
 */
@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class AccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    private String type;

    private String product;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fee = BigDecimal.ZERO;

    private String currency;

    private String state;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance;

    public String naturalKey() {
        return currency + "|" + startedAt + "|" + amount.setScale(2) + "|" + description;
    }
}
