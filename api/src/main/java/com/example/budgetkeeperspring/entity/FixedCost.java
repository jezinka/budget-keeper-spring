package com.example.budgetkeeperspring.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;

import java.math.BigDecimal;

@Where(clause = "deleted=false")
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class FixedCost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Version
    private Integer version;

    private String name;
    private BigDecimal amount;

    @ManyToOne()
    @Fetch(FetchMode.JOIN)
    private Circ circ;

    @Builder.Default
    private boolean deleted = Boolean.FALSE;
}
