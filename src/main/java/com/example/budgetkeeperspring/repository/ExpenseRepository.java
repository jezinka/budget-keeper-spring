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

    @Query("select e from Expense e left join fetch e.category c where e.transactionDate between :begin and :end")
    List<Expense> findAllByTransactionDateBetween(@Param("begin") LocalDate begin, @Param("end") LocalDate end);

    @Query("select e from Expense e left join fetch e.category c order by e.transactionDate desc")
    List<Expense> findAllByOrderByTransactionDateDesc();
}
