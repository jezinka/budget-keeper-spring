package com.example.budgetkeeperspring.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @ManyToOne()
    @Fetch(FetchMode.JOIN)
    private Category category;

    @ManyToOne()
    @Fetch(FetchMode.JOIN)
    private Circ circ;

    @Version
    private Integer version = 0;

}
