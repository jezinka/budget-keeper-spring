package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.FixedCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FixedCostRepository extends JpaRepository<FixedCost, Long> {

    @Query("SELECT fc, c FROM FixedCost fc JOIN FETCH fc.circ c")
    List<FixedCost> findAll();
}
