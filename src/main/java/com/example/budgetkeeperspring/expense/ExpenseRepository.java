package com.example.budgetkeeperspring.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("select cmt from CurrentMonthExpenses cmt ORDER BY CASE WHEN cmt.category is null THEN (-1000000 + cmt.amount) ELSE cmt.amount END")
    List<CurrentMonthExpenses> findAllForCurrentMonth();

    List<Expense> findAllByTransactionDateBetween(@Param("begin") Date begin, @Param("end") Date end);
}
