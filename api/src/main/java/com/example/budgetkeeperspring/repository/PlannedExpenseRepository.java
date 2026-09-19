package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.PlannedExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlannedExpenseRepository extends JpaRepository<PlannedExpense, Integer> {
    @Query("select pe from PlannedExpense pe where pe.plan.id = :planId order by pe.id")
    List<PlannedExpense> findAllByPlanId(@Param("planId") Integer planId);
}
