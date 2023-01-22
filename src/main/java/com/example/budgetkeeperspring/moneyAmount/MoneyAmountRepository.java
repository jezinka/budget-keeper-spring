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
        return namedParameterJdbcTemplate.queryForObject(
                "select * from current_month_money_amount;",
                new MapSqlParameterSource(),
                BeanPropertyRowMapper.newInstance(MoneyAmount.class));
    }
}
