package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.FixedCost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixedCostRepository extends JpaRepository<FixedCost, Long> {
}
