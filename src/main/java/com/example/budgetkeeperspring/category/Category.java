package com.example.budgetkeeperspring.category;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Boolean useInYearlyCharts;

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
