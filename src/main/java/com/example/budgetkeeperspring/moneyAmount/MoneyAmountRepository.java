package com.example.budgetkeeperspring.moneyAmount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Repository
public class MoneyAmountRepository {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    boolean addMoneyAmountForCurrentMonth(Float amount) {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("begin", begin)
                .addValue("amount", amount);

        int insertQuery = namedParameterJdbcTemplate.update("insert into money_amount (month, amount) values (:begin, :amount)", namedParameters);
        return insertQuery == 1;
    }

    MoneyAmount findMoneyAmountForCurrentMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("begin", begin)
                .addValue("end", end);

        return namedParameterJdbcTemplate.queryForObject(
                "select " +
                        " amount, " +
                        " income, " +
                        " expenses, " +
                        " (amount + income + expenses) as account_balance " +
                        "from (select ifnull(sum(case when t.amount > 0 then t.amount else 0 end), 0) as income, " +
                        "             ifnull(sum(case when t.amount < 0 then t.amount else 0 end), 0) as expenses " +
                        "      from transaction t " +
                        "      where transaction_date between cast(:begin AS DATE) and cast(:end AS DATE) " +
                        "         and is_deleted = 0 ) balance " +
                        "         join money_amount " +
                        "where month between cast(:begin AS DATE) and cast(:end AS DATE);",
                namedParameters,
                BeanPropertyRowMapper.newInstance(MoneyAmount.class));
    }
}
