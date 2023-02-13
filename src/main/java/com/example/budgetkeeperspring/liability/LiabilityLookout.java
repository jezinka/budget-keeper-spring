package com.example.budgetkeeperspring.liability;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class LiabilityLookout {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    Date date;
    Float outcome;

    @ManyToOne()
    Liability liability;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getOutcome() {
        return outcome;
    }

    public void setOutcome(Float outcome) {
        this.outcome = outcome;
    }

    public Liability getLiability() {
        return liability;
    }

    public void setLiability(Liability liability) {
        this.liability = liability;
    }
}
