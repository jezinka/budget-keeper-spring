package com.example.budgetkeeperspring.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import static java.time.temporal.TemporalAdjusters.*;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static java.time.LocalTime.now;

@Repository
public class TransactionRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    List<Transaction> findAllForCurrentMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

        return jdbcTemplate.query("select t.id, transaction_date, substring(title, 1, 50) as title, substring(payee,1, 50) as payee, amount, c.name as category " +
                        "from transaction t " +
                        "left join category c on t.category_id = c.id " +
                        "where transaction_date between ? and ? " +
                        "order by transaction_date asc",
                BeanPropertyRowMapper.newInstance(Transaction.class),
                begin, end);
    }
}
