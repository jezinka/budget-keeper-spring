package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.BudgetPlanDTO;
import com.example.budgetkeeperspring.dto.BudgetPlanSummaryDTO;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetPlanService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ExpenseRepository expenseRepository;

    public BudgetPlanService(NamedParameterJdbcTemplate namedParameterJdbcTemplate, ExpenseRepository expenseRepository) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.expenseRepository = expenseRepository;
    }

    public List<BudgetPlanDTO> getBudgetPlan(LocalDate startDate, LocalDate endDate) {
        String sql = """
                SELECT g.id, c.name, SUM(e.amount) AS expense, g.amount AS goal \
                FROM goal g \
                JOIN category c ON c.id = g.category_id \
                LEFT JOIN expense e ON e.category_id = c.id AND e.transaction_date >= cast(:startDate as DATE) AND e.transaction_date <= cast(:endDate as DATE) \
                WHERE g.amount IS NOT NULL AND g.date >= cast(:startDate as DATE) AND g.date <= cast(:endDate as DATE) \
                GROUP BY c.name \
                ORDER BY c.name""";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("startDate", startDate.toString());
        parameters.addValue("endDate", endDate.toString());

        return namedParameterJdbcTemplate.query(sql, parameters, (rs, rowNum) -> {
            BudgetPlanDTO budgetPlan = new BudgetPlanDTO();
            budgetPlan.setId(rs.getLong("id"));
            budgetPlan.setCategory(rs.getString("name"));
            budgetPlan.setExpense(rs.getBigDecimal("expense") == null ? BigDecimal.ZERO : rs.getBigDecimal("expense"));
            budgetPlan.setGoal(rs.getBigDecimal("goal") == null ? BigDecimal.ZERO : rs.getBigDecimal("goal"));
            return budgetPlan;
        });
    }

    public BudgetPlanSummaryDTO getBudgetPlanSummary(LocalDate startDate, LocalDate endDate) {
        BudgetPlanSummaryDTO summary = new BudgetPlanSummaryDTO();
        List<BudgetPlanDTO> budgetPlanDTOS = getBudgetPlan(startDate, endDate);

        BigDecimal sumPlanned = budgetPlanDTOS.stream().filter(budgetPlanDTO -> budgetPlanDTO.getGoal().compareTo(BigDecimal.ZERO) < 0).map(BudgetPlanDTO::getExpense).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumNoBuy = budgetPlanDTOS.stream().filter(budgetPlanDTO -> budgetPlanDTO.getGoal().compareTo(BigDecimal.ZERO) == 0).map(BudgetPlanDTO::getExpense).reduce(BigDecimal.ZERO, BigDecimal::add);

        summary.setSumPlanned(sumPlanned);
        summary.setNoBuy(sumNoBuy);
        summary.setOtherExpenses(getOtherExpensesSum(startDate, endDate));
        summary.setTotal(expenseRepository.sumAllByTransactionDateBetween(startDate, endDate));

        return summary;
    }

    private BigDecimal getOtherExpensesSum(LocalDate startDate, LocalDate endDate) {
        String sql = """
                select sum(e.amount) as expense \
                       from expense e \
                       join category c on e.category_id = c.id \
                       where e.category_id not in (select category_id from goal where date >= cast(:startDate as DATE)  and date <= cast(:endDate as DATE) ) \
                         and e.transaction_date >= cast(:startDate as DATE) \
                         and e.transaction_date <= cast(:endDate as DATE) \
                           and c.level in (0, 1, 3)""";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("startDate", startDate.toString());
        parameters.addValue("endDate", endDate.toString());

        return namedParameterJdbcTemplate.queryForObject(sql, parameters, BigDecimal.class);
    }
}