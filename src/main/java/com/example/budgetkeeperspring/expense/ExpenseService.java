package com.example.budgetkeeperspring.expense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class ExpenseService {

    private static final Long EMPTY_OPTION = -1L;
    private static final DateFormatSymbols DFS = new DateFormatSymbols(new Locale("pl", "PL"));

    @Autowired
    ExpenseRepository expenseRepository;

    Boolean updateTransaction(Long id, Expense updateExpense) {
        Expense expense = expenseRepository.getById(id);
        if (updateExpense != null) {
            setTransactionProperties(updateExpense, expense);
        }
        expenseRepository.save(expense);
        return true;
    }

    Boolean splitExpanse(Long id, List<Expense> updateExpenses) {
        Expense expense = expenseRepository.getById(id);
        for (Expense e : updateExpenses) {
            setTransactionProperties(expense, e);
            expenseRepository.save(e);
        }
        expenseRepository.deleteById(id);
        return true;
    }

    private static void setTransactionProperties(Expense updateExpense, Expense expense) {
        expense.setTransactionDate(updateExpense.getTransactionDate());
        expense.setTitle(updateExpense.getTitle());
        expense.setPayee(updateExpense.getPayee());
        expense.setAmount(updateExpense.getAmount());
        if (updateExpense.getCategory().getId() != EMPTY_OPTION) {
            expense.setCategory(updateExpense.getCategory());
        }
        if (updateExpense.getLiabilityId() != EMPTY_OPTION) {
            expense.setLiabilityId(updateExpense.getLiabilityId());
        }
    }

    public Map getDailyExpenses(LocalDate begin, LocalDate end) {
        List<Expense> expenses = expenseRepository.findAllByTransactionDateBetween(Date.valueOf(begin), Date.valueOf(end));

        return expenses.stream()
                .filter(e -> e.getAmount() < 0)
                .collect(Collectors.groupingBy(Expense::getTransactionDay,
                        Collectors.summingDouble(e -> Math.abs(e.getAmount()))));
    }

    public List<Expense> findAll(HashMap filters) {
        return expenseRepository.retrieveAll();
//       String query = "select * from transactions_category " +
//                "where 1=1  ";
//
//        if (filters.getOrDefault("onlyEmptyCategories", false).equals(true)) {
//            query += " and category_id is null ";
//        }
//        if (filters.getOrDefault("onlyEmptyLiabilities", false).equals(true)) {
//            query += " and liability_id is null ";
//        }
//        if (filters.getOrDefault("onlyExpenses", false).equals(true)) {
//            query += " and amount < 0 ";
//        }
//        if (filters.get("year") != null) {
//            query += " and year(transaction_date) = " + filters.get("year");
//        }
//        if (filters.get("month") != null) {
//            query += " and month(transaction_date) = " + filters.get("month");
//        }
//        if (filters.get("category") != null) {
//            query += " and category = \"" + filters.get("category") + "\"";
//        }
//
//        String titleFilter = (String) filters.getOrDefault("title", "");
//        if (!titleFilter.equals("")) {
//            query += " and title like \"%" + titleFilter + "%\" ";
//        }
//
//        String payeeFilter = (String) filters.getOrDefault("payee", "");
//        if (!payeeFilter.equals("")) {
//            query += " and payee like \"%" + payeeFilter + "%\" ";
//        }
//
//        query += " order by transaction_date desc";
//
//        return jdbcTemplate.query(query, BeanPropertyRowMapper.newInstance(Expense.class));
    }


    Map<Integer, Map<String, Double>> getYearAtGlance(int year) {
        List<Expense> yearlyExpenses = expenseRepository.findAllByYear(year);

        Map<Integer, Map<String, Double>> collect = yearlyExpenses.stream().collect(groupingBy(
                Expense::getTransactionMonth,
                groupingBy(Expense::getCategoryName, Collectors.summingDouble(Expense::getAmount))));

        collect.put(99, getCategorySummary(yearlyExpenses));
        collect.forEach((month, categories) ->
                categories.put("SUMA", categories.values().stream().reduce(Double::sum).get())
        );
        return collect;
    }

    private Map<String, Double> getCategorySummary(List<Expense> yearlyExpenses) {
        List<String> categories = yearlyExpenses.stream().map(Expense::getCategoryName).distinct().toList();

        Map<String, Double> categorySum = new HashMap<>();
        for (String c : categories) {
            categorySum.put(c,
                    yearlyExpenses
                            .stream()
                            .filter(e -> e.getCategoryName() == c)
                            .mapToDouble(Expense::getAmount).sum());
        }
        return categorySum;
    }

    public Map getMonthsPivot(int year) {
        String[] shortMonths = DFS.getShortMonths();
        List<Expense> yearlyExpenses = expenseRepository.findAllByYear(year);

        return yearlyExpenses.stream().collect(groupingBy(
                        Expense::getTransactionMonth,
                        groupingBy(Expense::getCategoryName, Collectors.summingDouble(Expense::getAmount))))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> shortMonths[e.getKey() - 1], Map.Entry::getValue));
    }

    Map getGroupedByCategory(Date begin, Date end) {

        List<Expense> yearlyExpenses = expenseRepository.findAllByTransactionDateBetween(begin, end);

        return yearlyExpenses
                .stream()
                .collect(groupingBy(Expense::getCategoryName,
                        Collectors.summingDouble(Expense::getAmount)));
    }
}
