package com.example.budgetkeeperspring.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

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
                        "and is_deleted = 0 " +
                        "order by transaction_date asc",
                BeanPropertyRowMapper.newInstance(Transaction.class),
                begin, end);
    }

    public Boolean deleteTransaction(Long id) {
        int result = jdbcTemplate.update("update transaction set is_deleted = 1 where id = ?", id);
        return result == 1;
    }

    public Boolean editTransaction(Transaction transaction) {
        int result = jdbcTemplate.update("update transaction set " +
                        "transaction_date = ?," +
                        "title = ?," +
                        "payee = ?," +
                        "amount = ?," +
                        "category_id = ? " +
                        "where id = ?",
                transaction.getTransactionDate(),
                transaction.getTitle(),
                transaction.getPayee(),
                transaction.getAmount(),
                transaction.getCategoryId(),
                transaction.getId());
        return result == 1;
    }

    public Transaction getById(Long id) {
        return jdbcTemplate.queryForObject("select t.id, transaction_date, title, payee, amount, category_id " +
                "from transaction t " +
                "where t.id = ?", BeanPropertyRowMapper.newInstance(Transaction.class), id);
    }
}
