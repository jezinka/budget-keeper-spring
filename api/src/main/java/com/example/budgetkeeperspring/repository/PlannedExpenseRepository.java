package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.PlannedExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlannedExpenseRepository extends JpaRepository<PlannedExpense, Integer> {
    @Query("select pe from PlannedExpense pe where pe.plan.id = :planId order by pe.id")
    List<PlannedExpense> findAllByPlanId(@Param("planId") Integer planId);

    @Query("select pe from PlannedExpense pe where pe.plan.id in (:planIds) order by pe.id")
    List<PlannedExpense> findAllByPlanIdIn(@Param("planIds") List<Integer> planIds);

    Optional<PlannedExpense> findFirstByNameAndPlan_StartDateLessThanEqualAndPlan_EndDateGreaterThanEqualOrderById(
            String name, LocalDate planStartDate, LocalDate planEndDate);

    boolean existsByPlan_IdAndRecurringPayment_Id(Integer planId, Integer recurringPaymentId);
}
