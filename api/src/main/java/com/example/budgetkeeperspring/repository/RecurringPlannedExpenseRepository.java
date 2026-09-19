package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.RecurringPlannedExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecurringPlannedExpenseRepository extends JpaRepository<RecurringPlannedExpense, Integer> {
    List<RecurringPlannedExpense> findAllByActiveTrueOrderByDueDayAscNameAsc();
}
