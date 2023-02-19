package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("select e from Expense e left join fetch e.category c where e.transactionDate between :begin and :end ORDER BY CASE WHEN e.category is null THEN (-1000000 + e.amount) ELSE e.amount END")
    List<Expense> findAllForTimePeriod(@Param("begin") LocalDate begin, @Param("end") LocalDate end);

    List<Expense> findAllByTransactionDateBetween(LocalDate begin, LocalDate end);

    List<Expense> findAllByOrderByTransactionDateDesc();
}
