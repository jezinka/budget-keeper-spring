package com.example.budgetkeeperspring.expense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
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

    @Transactional
    Boolean splitExpanse(Long id, List<Expense> updateExpenses) {
        expenseRepository.saveAll(updateExpenses);
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

    public List getDailyExpenses(LocalDate begin, LocalDate end) {
        List<Expense> expenses = expenseRepository.findAllByTransactionDateBetween(Date.valueOf(begin), Date.valueOf(end));
        List<DailyExpenses> list = new ArrayList<>();

        expenses.stream()
                .filter(e -> e.getAmount() < 0)
                .collect(Collectors.groupingBy(Expense::getTransactionDay,
                        Collectors.summingDouble(e -> Math.abs(e.getAmount()))))
                .forEach((key, value) -> list.add(new DailyExpenses(key, value.floatValue())));
        return list;
    }

    public List<Expense> findAll(HashMap<String, Object> filters) {
        List<Predicate<Expense>> allPredicates = new ArrayList<>();

        if (filters.getOrDefault("onlyEmptyCategories", false).equals(true)) {
            allPredicates.add(p -> p.getCategory() == null);
        }
        if (filters.getOrDefault("onlyEmptyLiabilities", false).equals(true)) {
            allPredicates.add(p -> p.getLiabilityId() == null);
        }
        if (filters.getOrDefault("onlyExpenses", false).equals(true)) {
            allPredicates.add(p -> p.getAmount() < 0);
        }
        if (filters.get("year") != null) {
            allPredicates.add(p -> p.getTransactionYear() == (int) filters.get("year"));
        }
        if (filters.get("month") != null) {
            allPredicates.add(p -> p.getTransactionMonth() == (int) filters.get("month"));
        }
        if (filters.get("category") != null) {
            allPredicates.add(p -> p.getCategoryName() == filters.get("category").toString());
        }
        if (!filters.getOrDefault("title", "").equals("")) {
            allPredicates.add(p -> p.getTitle().toLowerCase().contains(filters.get("title").toString().toLowerCase()));
        }
        if (!filters.getOrDefault("payee", "").equals("")) {
            allPredicates.add(p -> p.getPayee().toLowerCase().contains(filters.get("payee").toString().toLowerCase()));
        }

        if (allPredicates.size() == 0) {
            allPredicates.add(p -> true);
        }

        return expenseRepository.retrieveAll()
                .stream()
                .filter(allPredicates.stream().reduce(x -> true, Predicate::and))
                .toList();
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

    public List getMonthsPivot(int year) {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] shortMonths = DFS.getShortMonths();
        List<Expense> yearlyExpenses = expenseRepository.findAllByYear(year);

        yearlyExpenses.stream().collect(groupingBy(
                        Expense::getCategoryName,
                        groupingBy(Expense::getTransactionMonth, Collectors.summingDouble(Expense::getAmount))))
                .forEach((category, entry) -> {
                    Map chartEntry = new HashMap();
                    chartEntry.put("category", category);
                    for (Map.Entry<Integer, Double> e : entry.entrySet()) {
                        Integer month = e.getKey();
                        Double amount = e.getValue();
                        chartEntry.put(shortMonths[month - 1], amount.floatValue());
                    }
                    list.add(chartEntry);
                });

        return list;
    }

    List getGroupedByCategory(Date begin, Date end) {
        List<MonthCategoryAmount> list = new ArrayList<>();
        List<Expense> yearlyExpenses = expenseRepository.findAllByTransactionDateBetween(begin, end);

        yearlyExpenses
                .stream()
                .collect(groupingBy(Expense::getTransactionMonth,
                        groupingBy(Expense::getCategoryName,
                                Collectors.summingDouble(Expense::getAmount))))
                .forEach((month, value) -> value.forEach((category, amount) ->
                        list.add(new MonthCategoryAmount(month, category, (float) Math.abs(amount))))
                );
        return list;
    }
}
