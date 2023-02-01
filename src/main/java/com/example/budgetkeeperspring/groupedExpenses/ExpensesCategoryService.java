package com.example.budgetkeeperspring.groupedExpenses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class ExpensesCategoryService {

    private static final DateFormatSymbols DFS = new DateFormatSymbols(new Locale("pl", "PL"));

    @Autowired
    ExpensesCategoryRepository expensesCategoryRepository;

    Map<Integer, Map<String, Double>> getYearAtGlance(int year) {
        List<ExpensesCategory> yearlyExpenses = expensesCategoryRepository.findAllByYear(year);

        Map<Integer, Map<String, Double>> collect = yearlyExpenses.stream().collect(groupingBy(
                ExpensesCategory::getTransactionMonth,
                groupingBy(ExpensesCategory::getCategory, Collectors.summingDouble(ExpensesCategory::getAmount))));

        collect.put(99, getCategorySummary(yearlyExpenses));
        collect.forEach((month, categories) ->
                categories.put("SUMA", categories.values().stream().reduce(Double::sum).get())
        );
        return collect;
    }

    private Map<String, Double> getCategorySummary(List<ExpensesCategory> yearlyExpenses) {
        List<String> categories = yearlyExpenses.stream().map(ExpensesCategory::getCategory).distinct().toList();

        Map<String, Double> categorySum = new HashMap<>();
        for (String c : categories) {
            categorySum.put(c,
                    yearlyExpenses
                            .stream()
                            .filter(e -> e.getCategory() == c)
                            .mapToDouble(ExpensesCategory::getAmount).sum());
        }
        return categorySum;
    }

    public Map getMonthsPivot(int year) {
        String[] shortMonths = DFS.getShortMonths();
        List<ExpensesCategory> yearlyExpenses = expensesCategoryRepository.findAllByYear(year);

        return yearlyExpenses.stream().collect(groupingBy(
                        ExpensesCategory::getTransactionMonth,
                        groupingBy(ExpensesCategory::getCategory, Collectors.summingDouble(ExpensesCategory::getAmount))))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> shortMonths[e.getKey() - 1], Entry::getValue));
    }

    Map getGroupedByCategory(Date begin, Date end) {

        List<ExpensesCategory> yearlyExpenses = expensesCategoryRepository.findAllByTransactionDateBetween(begin, end);

        return yearlyExpenses
                .stream()
                .collect(groupingBy(ExpensesCategory::getCategory,
                        Collectors.summingDouble(ExpensesCategory::getAmount)));
    }
}
