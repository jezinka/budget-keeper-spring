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
                "select amount, income, outcome, amount + income + outcome as account_balance " +
                        "from (select m.amount," +
                        "             sum(case when t.amount > 0 then t.amount else 0 end) as income, " +
                        "             sum(case when t.amount < 0 then t.amount else 0 end) as outcome " +
                        "      from transaction t " +
                        "               cross join money_amount m " +
                        "      where transaction_date between :begin and :end " +
                        "        and month between :begin and :end) z", namedParameters, new MoneyAmountRowMapper());
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
