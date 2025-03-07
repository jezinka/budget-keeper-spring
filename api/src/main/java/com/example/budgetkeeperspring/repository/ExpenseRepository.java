package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.dto.LifestyleInflationRecordDTO;
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

    @Query("select e from Expense e left join fetch e.category c where e.transactionDate between :begin and :end and c.level <> 2")
    List<Expense> findAllByTransactionDateBetweenWithoutInvestments(LocalDate begin, LocalDate end);

    @Query("select new com.example.budgetkeeperspring.dto.LifestyleInflationRecordDTO(" +
           "CAST(t.date AS string), t.amount, t.name, t.categoryLevel) " +
           "from (" +
           "    select DATE_FORMAT(e.transactionDate, '%Y-%m') as date, sum(e.amount) as amount, c.name as name, cl.name as categoryLevel " +
           "    from Expense e " +
           "    join e.category c " +
           "    left join CategoryLevel cl on cl.level = c.level " +
           "    group by DATE_FORMAT(e.transactionDate, '%Y-%m'), c.name, c.level) t " +
           " where t.categoryLevel != 'Wpływy' and t.categoryLevel != 'Kredyt'")
    List<LifestyleInflationRecordDTO> countSumOfExpensesByMonthAndCategory();
}
