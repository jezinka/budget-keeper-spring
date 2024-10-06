package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.BudgetPlanDTO;
import com.example.budgetkeeperspring.dto.BudgetPlanSummaryDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.Goal;
import com.example.budgetkeeperspring.repository.CategoryRepository;
import com.example.budgetkeeperspring.repository.GoalRepository;
import com.example.budgetkeeperspring.utils.DateUtils;
import com.example.budgetkeeperspring.utils.FileService;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.example.budgetkeeperspring.service.CategoryService.UNKNOWN_CATEGORY;

@Service
public class BudgetPlanService {

    public static final String CATEGORY_HEADER = "Kategoria";
    public static final String AMOUNT_HEADER = "Kwota";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final CategoryRepository categoryRepository;
    private final GoalRepository goalRepository;

    public BudgetPlanService(NamedParameterJdbcTemplate namedParameterJdbcTemplate, CategoryRepository categoryRepository, GoalRepository goalRepository) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.categoryRepository = categoryRepository;
        this.goalRepository = goalRepository;
    }

    public List<BudgetPlanDTO> getBudgetPlan(LocalDate startDate, LocalDate endDate) {
        String sql = """
                SELECT g.id, c.name, SUM(e.amount) AS expense, count(e.amount) AS transactionCount, g.amount AS goal \
                FROM goal g \
                JOIN category c ON c.id = g.category_id \
                LEFT JOIN expense e ON e.category_id = c.id AND e.transaction_date >= cast(:startDate as DATE) AND e.transaction_date <= cast(:endDate as DATE) AND !e.deleted \
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
            budgetPlan.setTransactionCount(rs.getInt("transactionCount"));
            return budgetPlan;
        });
    }

    public BudgetPlanSummaryDTO getBudgetPlanSummary(LocalDate startDate, LocalDate endDate) {
        BudgetPlanSummaryDTO summary = new BudgetPlanSummaryDTO();

        summary.setInvestments(getInvestmentsSum(startDate, endDate));
        summary.setNoPayGoal(getNotPayedGoalExpensesSum(startDate, endDate));

        return summary;
    }

    private BigDecimal getInvestmentsSum(LocalDate startDate, LocalDate endDate) {
        String sql = """
                select sum(e.amount) as expense \
                       from expense e \
                       join category c on e.category_id = c.id \
                       where e.category_id not in (select category_id from goal where date >= cast(:startDate as DATE)  and date <= cast(:endDate as DATE) ) \
                         and e.transaction_date >= cast(:startDate as DATE) \
                         and e.transaction_date <= cast(:endDate as DATE) AND !e.deleted \
                         and c.level = 2""";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("startDate", startDate.toString());
        parameters.addValue("endDate", endDate.toString());

        return namedParameterJdbcTemplate.queryForObject(sql, parameters, BigDecimal.class);
    }

    private BigDecimal getNotPayedGoalExpensesSum(LocalDate startDate, LocalDate endDate) {
        String sql = """
                select sum(difference)
                                        from (SELECT IF(sum(e.amount) is not null, g.amount - sum(e.amount), g.amount) AS difference
                                              FROM goal g
                                                       LEFT JOIN expense e
                                                                 ON e.category_id = g.category_id AND e.transaction_date >= CAST(:startDate as DATE) AND
                                                                    e.transaction_date <= CAST(:endDate as DATE) AND !e.deleted
                                              WHERE  g.amount < 0
                                                AND g.date >= CAST(:startDate as DATE)
                                                AND g.date <= CAST(:endDate as DATE)
                                              GROUP BY g.category_id, g.amount) as diff
                                        where difference < 0;""";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("startDate", startDate.toString());
        parameters.addValue("endDate", endDate.toString());

        return namedParameterJdbcTemplate.queryForObject(sql, parameters, BigDecimal.class);
    }

    public boolean createGoalsFromFile(MultipartFile file) {
        try (CSVReader csvReader = new CSVReader(new FileReader(FileService.getTempFile(file)))) {

            if (!validateCSV(csvReader.readNext())) {
                return false;
            }

            String[] values;
            while ((values = csvReader.readNext()) != null) {
                List<String> list = Arrays.asList(values);
                createGoal(list);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Failed to read from file: " + e.getMessage());
        } catch (CsvValidationException e) {
            System.err.println("Invalid csv file: " + e.getMessage());
        }
        return false;
    }

    private boolean validateCSV(String[] header) {
        if (header.length != 2) {
            System.err.println("Invalid csv file: Invalid number of columns");
            return false;
        }

        if (!Arrays.equals(header, new String[]{CATEGORY_HEADER, AMOUNT_HEADER})) {
            System.err.println("Invalid csv file: Invalid headers");
            return false;
        }
        return true;
    }

    public void createGoal(List<String> list) {
        Category categoryByName = categoryRepository.findCategoryByName(list.get(0));
        BigDecimal amount = new BigDecimal(list.get(1).replace(",", "."));
        LocalDate beginOfCurrentMonth = DateUtils.getBeginOfCurrentMonth();
        goalRepository
                .findGoalByCategoryAndDate(categoryByName, beginOfCurrentMonth)
                .ifPresentOrElse(
                        g -> {
                            g.setAmount(g.getAmount().add(amount));
                            goalRepository.save(g);
                        },
                        () -> {
                            Goal goal = new Goal();
                            goal.setCategory(categoryByName);
                            goal.setAmount(amount);
                            goal.setDate(beginOfCurrentMonth);
                            goalRepository.save(goal);
                        });
    }

    public String writeDataToCsv() {
        String[] header = {CATEGORY_HEADER, AMOUNT_HEADER};
        List<String> data = categoryRepository.findAll()
                .stream()
                .filter(c -> c.getId() != UNKNOWN_CATEGORY)
                .filter(c -> c.getLevel() != null && List.of(0, 1, 3).contains(c.getLevel()))
                .map(Category::getName)
                .toList();

        StringWriter writer = new StringWriter();

        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(header);
            for (String row : data) {
                csvWriter.writeNext(new String[]{row, "0.00"});
            }
            System.out.println("CSV file created successfully.");
        } catch (IOException e) {
            System.err.println("Failed to create CSV file: " + e.getMessage());
        }
        return writer.toString();
    }
}