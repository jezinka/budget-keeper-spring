package com.example.budgetkeeperspring.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("select e from Expense e left join e.category where e.transactionDate between :begin and :end ORDER BY CASE WHEN e.category is null THEN (-1000000 + e.amount) ELSE e.amount END")
    List<Expense> findAllForCurrentMonth(Date begin, Date end);

    @Query("select e from Expense e left join e.category where function('year', e.transactionDate) = :year")
    List<Expense> findAllByYear(@Param("year") int year);

    @Query("select e from Expense e left join e.category where e.transactionDate between :begin and :end")
    List<Expense> findAllByTransactionDateBetween(Date begin, Date end);

    @Query("select e from Expense e left join e.category where e.id = :id")
    Expense getById(Long id);

    @Query("select e from Expense e left join e.category c")
    List<Expense> retrieveAll();
}
