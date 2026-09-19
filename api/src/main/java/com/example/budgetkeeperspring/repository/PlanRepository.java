package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Integer> {
    Optional<Plan> findByStartDate(LocalDate startDate);
}
