package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.DailyExpensesDTO;
import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.MonthCategoryAmountDTO;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.mapper.ExpenseMapper;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

@Service
public class ExpenseService {

    private static final DateFormatSymbols DFS = new DateFormatSymbols(new Locale("pl", "PL"));
    private static final String CATEGORY = "category";

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    public Boolean updateTransaction(Long id, ExpenseDTO updateExpenseDTO) {
        boolean expenseExists = expenseRepository.existsById(id);
        if (expenseExists) {
            Expense expense = expenseMapper.mapToEntity(updateExpenseDTO);
            expense.setId(id);
            expenseRepository.save(expense);
            return true;
        }
        return false;
    }

    @Transactional
    public Boolean splitExpense(Long id, List<ExpenseDTO> updateExpensesDTOs) {
        List<Expense> updateExpenses = updateExpensesDTOs.stream().map(expenseMapper::mapToEntity).toList();
        expenseRepository.saveAll(updateExpenses);
        expenseRepository.deleteById(id);
        return true;
    }

    public List<DailyExpensesDTO> getDailyExpenses(LocalDate begin, LocalDate end) {
        List<Expense> expenses = expenseRepository.findAllByTransactionDateBetween(begin, end);
        List<DailyExpensesDTO> list = new ArrayList<>();

        expenses.stream()
                .filter(e -> e.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.groupingBy(Expense::getTransactionDay,
                        reducing(BigDecimal.ZERO, e -> e.getAmount().abs(), BigDecimal::add)))
                .forEach((key, value) -> list.add(new DailyExpensesDTO(key, value)));
        return list;
    }

    public List<Expense> findAll(Map<String, Object> filters) {
        List<Predicate<Expense>> allPredicates = new ArrayList<>();

        if (filters.getOrDefault("onlyEmptyCategories", false).equals(true)) {
            allPredicates.add(p -> p.getCategory() == null);
        }
        if (filters.getOrDefault("onlyExpenses", false).equals(true)) {
            allPredicates.add(p -> p.getAmount().compareTo(new BigDecimal(0)) < 0);
        }
        if (filters.get("year") != null) {
            allPredicates.add(p -> p.getTransactionYear() == (int) filters.get("year"));
        }
        if (filters.get("month") != null) {
            allPredicates.add(p -> p.getTransactionMonth() == (int) filters.get("month"));
        }
        if (filters.get(CATEGORY) != null) {
            allPredicates.add(p -> p.getCategoryName().equals(filters.get(CATEGORY).toString()));
        }
        if (!filters.getOrDefault("title", "").equals("")) {
            allPredicates.add(p -> p.getTitle().toLowerCase().contains(filters.get("title").toString().toLowerCase()));
        }
        if (!filters.getOrDefault("payee", "").equals("")) {
            allPredicates.add(p -> p.getPayee().toLowerCase().contains(filters.get("payee").toString().toLowerCase()));
        }

        if (allPredicates.isEmpty()) {
            allPredicates.add(p -> true);
        }

        return expenseRepository.retrieveAll()
                .stream()
                .filter(allPredicates.stream().reduce(x -> true, Predicate::and))
                .toList();
    }


    public Map<Integer, Map<String, BigDecimal>> getYearAtGlance(int year) {
        List<Expense> yearlyExpenses = expenseRepository.findAllByYear(year);

        Map<Integer, Map<String, BigDecimal>> collect = yearlyExpenses.stream().collect(groupingBy(
                Expense::getTransactionMonth,
                groupingBy(Expense::getCategoryName, reducing(BigDecimal.ZERO,
                        Expense::getAmount, BigDecimal::add))));

        collect.put(99, getCategorySummary(yearlyExpenses));
        collect.forEach((month, categories) ->
                categories.put("SUMA", categories.values().stream().reduce(BigDecimal::add).get())
        );
        return collect;
    }

    private Map<String, BigDecimal> getCategorySummary(List<Expense> yearlyExpenses) {
        List<String> categories = yearlyExpenses.stream().map(Expense::getCategoryName).distinct().toList();

        Map<String, BigDecimal> categorySum = new HashMap<>();
        for (String c : categories) {
            categorySum.put(c,
                    yearlyExpenses
                            .stream()
                            .filter(e -> e.getCategoryName().equals(c))
                            .map(Expense::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return categorySum;
    }

    public List<Map<String, Object>> getMonthsPivot(int year) {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] shortMonths = DFS.getShortMonths();
        List<Expense> yearlyExpenses = expenseRepository.findAllByYear(year);

        yearlyExpenses.stream().collect(groupingBy(
                        Expense::getCategoryName,
                        groupingBy(Expense::getTransactionMonth, reducing(BigDecimal.ZERO,
                                Expense::getAmount, BigDecimal::add))))
                .forEach((category, entry) -> {
                    Map<String, Object> chartEntry = new LinkedHashMap<>();
                    chartEntry.put(CATEGORY, category);
                    for (Map.Entry<Integer, BigDecimal> e : entry.entrySet()) {
                        Integer month = e.getKey();
                        BigDecimal amount = e.getValue();
                        chartEntry.put(shortMonths[month - 1], amount);
                    }
                    list.add(chartEntry);
                });

        return list;
    }

    public List<MonthCategoryAmountDTO> getGroupedByCategory(LocalDate begin, LocalDate end) {
        List<MonthCategoryAmountDTO> list = new ArrayList<>();
        List<Expense> yearlyExpenses = expenseRepository.findAllByTransactionDateBetween(begin, end);

        yearlyExpenses
                .stream()
                .collect(groupingBy(Expense::getTransactionMonth,
                        groupingBy(Expense::getCategoryName,
                                reducing(BigDecimal.ZERO,
                                        Expense::getAmount, BigDecimal::add))))
                .forEach((month, value) -> value.forEach((category, amount) ->
                        list.add(new MonthCategoryAmountDTO(month, category, amount.abs())))
                );
        return list;
    }
}
