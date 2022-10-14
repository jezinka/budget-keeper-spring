package com.example.budgetkeeperspring.groupedExpenses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public class GroupedExpensesRepository {

    private static final DateFormatSymbols DFS = new DateFormatSymbols(new Locale("pl", "PL"));

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
        return jdbcTemplate.query("" +
                "select month, category, abs(amount) as amount " +
                " from (select month(transaction_date) as month, c.name as category, round(sum(amount), 2) as amount " +
                "      from transaction t " +
                "               join category c on t.category_id = c.id " +
                "      where transaction_date between ? and ? " +
                "        and is_deleted <> 1 " +
                "      group by month, category " +
                "      order by amount) t " +
                " where amount < 0 ", BeanPropertyRowMapper.newInstance(GroupedExpenses.class), begin, end);
    }

    public List getMonthsPivot(int year) {

        String[] shortMonths = DFS.getShortMonths();
        String pivotColumns = IntStream.range(1, shortMonths.length).mapToObj(monthIdx ->
                "round(sum(IF(month(transaction_date) = " + monthIdx + ", t.amount, 0)),2) as " + shortMonths[monthIdx - 1]
        ).collect(Collectors.joining(", "));

        return jdbcTemplate.queryForList("select c.name as category, " + pivotColumns +
                " from transaction t " +
                "         left join category c on t.category_id = c.id " +
                " where is_deleted = 0 " +
                " and c.use_in_yearly_charts = 1" +
                "  and year(transaction_date) = ?" +
                " group by category;", year);
    }
}
