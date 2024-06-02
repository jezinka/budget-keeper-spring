package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    @Query(value = "SELECT SUM(g.amount) FROM Goal g WHERE g.date >= :startDate AND g.date <= :endDate")
    BigDecimal getGoalSum(LocalDate startDate, LocalDate endDate);
}
