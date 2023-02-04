package com.example.budgetkeeperspring.category;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Boolean useInYearlyCharts;

    public Category(String name) {
        this.name = name;
        this.useInYearlyCharts = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getUseInYearlyCharts() {
        return useInYearlyCharts;
    }

    public void setUseInYearlyCharts(Boolean useInYearlyCharts) {
        this.useInYearlyCharts = useInYearlyCharts;
    }
}
