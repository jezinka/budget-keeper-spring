package com.example.budgetkeeperspring.groupedExpenses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ExpensesCategoryRepository extends JpaRepository<ExpensesCategory, Long> {
    @Query("select ec from ExpensesCategory ec where function('year', ec.transactionDate) = :year")
    List<ExpensesCategory> findAllByYear(@Param("year") int year);

    List<ExpensesCategory> findAllByTransactionDateBetween(Date begin, Date end);
}
