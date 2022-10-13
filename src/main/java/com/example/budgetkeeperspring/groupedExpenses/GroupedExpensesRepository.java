package com.example.budgetkeeperspring.groupedExpenses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class GroupedExpensesRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    List<GroupedExpenses> getYearAtGlance(int year) {
        return jdbcTemplate.query("select month(transaction_date) as month, c.name as category, round(sum(amount), 2) as amount " +
                "from transaction t " +
                "         join category c on t.category_id = c.id " +
                "where year(transaction_date) = ? " +
                "  and is_deleted <> 1 " +
                "group by month, category", BeanPropertyRowMapper.newInstance(GroupedExpenses.class), year);
    }

    public List<GroupedExpenses> getCategorySumRows(int year) {
        return jdbcTemplate.query("select 99 as month, c.name as category, round(sum(amount), 2) as amount " +
                "from transaction t " +
                "         join category c on t.category_id = c.id " +
                "where year(transaction_date) = ? " +
                "  and is_deleted <> 1 " +
                "group by category", BeanPropertyRowMapper.newInstance(GroupedExpenses.class), year);
    }

    public List<GroupedExpenses> getMonthSumRows(int year) {
        return jdbcTemplate.query("select month(transaction_date) as month, 'SUMA' as category, round(sum(amount), 2) as amount " +
                "from transaction t " +
                "where year(transaction_date) = ? " +
                "  and is_deleted <> 1 " +
                "  and category_id is not null " +
                "group by month", BeanPropertyRowMapper.newInstance(GroupedExpenses.class), year);
    }

    List<GroupedExpenses> getGroupedByCategory(LocalDate begin, LocalDate end) {
        return jdbcTemplate.query("select month(transaction_date) as month, c.name as category, round(sum(amount), 2) as amount " +
                "from transaction t " +
                "         join category c on t.category_id = c.id " +
                "where transaction_date between ? and ? " +
                "  and is_deleted <> 1 " +
                "group by month, category " +
                "order by amount", BeanPropertyRowMapper.newInstance(GroupedExpenses.class), begin, end);
    }
}
