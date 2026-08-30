package com.example.budgetkeeperspring.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table
public class Beneficiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Lob
    @Column
    private String name;

    @OneToMany(mappedBy = "beneficiary")
    private Set<Expense> expenses = new LinkedHashSet<>();


}