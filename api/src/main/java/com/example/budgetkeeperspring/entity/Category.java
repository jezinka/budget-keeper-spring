package com.example.budgetkeeperspring.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Version
    private Integer version;

    @NotNull
    @NotBlank
    @Column(unique = true)
    private String name;

    @Builder.Default
    private boolean useInYearlyCharts = Boolean.TRUE;

    @Builder.Default
    private boolean toCheck = Boolean.FALSE;

    private Integer level;

    public Category(String name) {
        this.name = name;
    }
}
