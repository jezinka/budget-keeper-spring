package com.example.budgetkeeperspring.moneyAmount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Repository
public class MoneyAmountRepository {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
                        " outcome, " +
                        " (amount + income + outcome) as account_balance " +
                        "from (select ifnull(sum(case when t.amount > 0 then t.amount else 0 end), 0) as income, " +
                        "             ifnull(sum(case when t.amount < 0 then t.amount else 0 end), 0) as outcome " +
                        "      from transaction t " +
                        "      where transaction_date between cast(:begin AS DATE) and cast(:end AS DATE)) balance " +
                        "         join money_amount " +
                        "where month between cast(:begin AS DATE) and cast(:end AS DATE);", namedParameters, new MoneyAmountRowMapper());
    }
}

class MoneyAmountRowMapper implements RowMapper<MoneyAmount> {
    @Override
    public MoneyAmount mapRow(ResultSet rs, int rowNum) throws SQLException {
        MoneyAmount moneyAmount = new MoneyAmount();

        moneyAmount.setAmount(rs.getFloat("AMOUNT"));
        moneyAmount.setIncome(rs.getFloat("INCOME"));
        moneyAmount.setOutcome(rs.getFloat("OUTCOME"));
        moneyAmount.setAccountBalance(rs.getFloat("ACCOUNT_BALANCE"));

        return moneyAmount;
    }
}
