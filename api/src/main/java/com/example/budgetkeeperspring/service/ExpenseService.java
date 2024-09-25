package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.*;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.mapper.ExpenseMapper;
import com.example.budgetkeeperspring.repository.CategoryRepository;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import com.example.budgetkeeperspring.utils.DateUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExpenseService {

    private static final DateFormatSymbols DFS = new DateFormatSymbols(new Locale("pl", "PL"));
    private static final String CATEGORY = "category";

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseMapper expenseMapper;
    private final GoalService goalService;

    public ExpenseDTO createExpense(ExpenseDTO expenseDTO, Category category) {
        Expense expense = expenseMapper.mapToEntity(expenseDTO);
        expense.setCategory(category);
        return expenseMapper.mapToDto(expenseRepository.save(expense));
    }

    public Optional<ExpenseDTO> updateExpense(Long id, ExpenseDTO updateExpenseDTO) {
        AtomicReference<Optional<ExpenseDTO>> atomicReference = new AtomicReference<>();

        expenseRepository.findById(id).ifPresentOrElse(foundExpense -> {
            foundExpense.setAmount(updateExpenseDTO.getAmount());
            if (updateExpenseDTO.getCategoryId() == -1) {
                foundExpense.setCategory(null);
            } else {
                categoryRepository.findById(updateExpenseDTO.getCategoryId()).ifPresentOrElse(
                        foundExpense::setCategory,
                        () -> {
                            throw new NotFoundException();
                        }
                );
            }
            expenseRepository.save(foundExpense);
            atomicReference.set(Optional.of(expenseMapper.mapToDto(foundExpense)));
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }

    @Transactional
    public void splitExpense(Long id, List<ExpenseDTO> updateExpensesDTOs) {
        log.debug("Expense (" + id + ") will be splitted and deleted");

        List<Expense> updateExpenses = updateExpensesDTOs.stream().map(expenseMapper::mapToEntity).toList();
        updateExpenses.forEach(expense -> {
            Category category = expense.getCategory();
            if (category != null) {
                Category savedCategory = categoryRepository.findById(category.getId()).orElseThrow(NotFoundException::new);
                expense.setCategory(savedCategory);
            }
        });
        List<Expense> updated = expenseRepository.saveAll(updateExpenses);
        expenseRepository.deleteById(id);

        String newIds = updated.stream().map(e -> e.getId().toString()).collect(joining(","));
        log.debug("Expense (" + id + ") deleted. Expenses " + newIds + " created");
    }

    public List<DailyExpensesDTO> getDailyExpenses(LocalDate begin, LocalDate end) {
        List<Expense> expenses = expenseRepository.findAllByTransactionDateBetween(begin, end);
        List<DailyExpensesDTO> list = new ArrayList<>();

        expenses.stream()
                .filter(e -> e.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .collect(groupingBy(Expense::getTransactionDay,
                        reducing(BigDecimal.ZERO, e -> e.getAmount().abs(), BigDecimal::add)))
                .forEach((key, value) -> list.add(new DailyExpensesDTO(key, value)));
        return list;
    }

    public List<ExpenseDTO> findAll(Map<String, Object> filters) {
        List<Predicate<Expense>> allPredicates = new ArrayList<>();

        if (filters.getOrDefault("onlyEmptyCategories", false).equals(true)) {
            allPredicates.add(p -> p.getCategory() == null);
        }
        if (filters.getOrDefault("onlyExpenses", false).equals(true)) {
            allPredicates.add(p -> p.getAmount().compareTo(BigDecimal.ZERO) < 0);
        }
        if (filters.get("year") != null) {
            allPredicates.add(p -> p.getTransactionYear() == Integer.parseInt(filters.get("year").toString()));
        }
        if (filters.get("month") != null) {
            allPredicates.add(p -> p.getTransactionMonth() == Integer.parseInt(filters.get("month").toString()));
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

        return expenseRepository.findAllByOrderByTransactionDateDesc()
                .stream()
                .filter(allPredicates.stream().reduce(x -> true, Predicate::and))
                .map(expenseMapper::mapToDto)
                .toList();
    }


    public List<MonthCategoryAmountDTO> getYearAtGlance(int year) {
        LocalDate begin = DateUtils.getBeginOfSelectedYear(year);
        LocalDate end = DateUtils.getEndOfSelectedYear(year);

        List<MonthCategoryAmountDTO> groupedExpenses = new ArrayList<>();
        List<GoalDTO> goals = goalService.findAllForYear(year);

        List<Expense> yearlyExpenses = expenseRepository.findAllByTransactionDateBetween(begin, end)
                .stream()
                .filter(p -> p.getCategory() != null && p.getCategory().isUseInYearlyCharts())
                .toList();

        yearlyExpenses.stream()
                .collect(groupingBy(
                        Expense::getTransactionMonth,
                        groupingBy(Expense::getCategoryName, reducing(BigDecimal.ZERO,
                                Expense::getAmount, BigDecimal::add))))
                .forEach((month, value) -> value.forEach((category, amount) -> {
                    long transactionCount = yearlyExpenses.stream().filter(y -> y.getCategory().getName().equals(category) && y.getTransactionMonth() == month).count();
                    groupedExpenses.add(new MonthCategoryAmountDTO(month, category, amount, transactionCount));
                }));

        goals.forEach(g ->
        {
            MonthCategoryAmountDTO monthCategoryAmountDTO = groupedExpenses.stream()
                    .filter(expense -> expense.getMonth() == g.getDate().getMonthValue() && expense.getCategory().equals(g.getCategoryName()))
                    .findFirst()
                    .orElseGet(() -> {
                        MonthCategoryAmountDTO dto = new MonthCategoryAmountDTO(g.getDate().getMonthValue(), g.getCategoryName(), BigDecimal.ZERO);
                        groupedExpenses.add(dto);
                        return dto;
                    });
            monthCategoryAmountDTO.setGoalAmount(g.getAmount());
        });

        return groupedExpenses;
    }

    public List<Map<String, Object>> getMonthsPivot(int year) {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] shortMonths = DFS.getShortMonths();

        LocalDate begin = DateUtils.getBeginOfSelectedYear(year);
        LocalDate end = DateUtils.getEndOfSelectedYear(year);

        List<Expense> yearlyExpenses = expenseRepository.findAllByTransactionDateBetween(begin, end);

        yearlyExpenses.stream()
                .filter(p -> p.getCategory() != null && p.getCategory().isUseInYearlyCharts())
                .collect(groupingBy(
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

    public List<MonthCategoryAmountDTO> getGroupedByCategory(LocalDate begin, LocalDate end, boolean withInvestments) {
        List<MonthCategoryAmountDTO> list = new ArrayList<>();
        List<Expense> yearlyExpenses;
        if (withInvestments) {
            yearlyExpenses = expenseRepository.findAllByTransactionDateBetween(begin, end);
        } else {
            yearlyExpenses = expenseRepository.findAllByTransactionDateBetweenWithoutExpenses(begin, end);
        }

        yearlyExpenses
                .stream()
                .filter(e -> e.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .collect(groupingBy(Expense::getTransactionMonth,
                        groupingBy(Expense::getCategoryName,
                                reducing(BigDecimal.ZERO,
                                        Expense::getAmount, BigDecimal::add))))
                .forEach((month, value) -> value.forEach((category, amount) ->
                        list.add(new MonthCategoryAmountDTO(month, category, amount.abs())))
                );
        return list.stream().sorted(Comparator.comparing(MonthCategoryAmountDTO::getAmount).reversed()).toList();
    }

    public Optional<ExpenseDTO> findById(Long id) {
        return Optional.ofNullable(expenseMapper.mapToDto(expenseRepository.findById(id)
                .orElse(null)));
    }

    public List<ExpenseDTO> findAllByTransactionDateBetween(LocalDate begin, LocalDate end) {
        return expenseRepository
                .findAllByTransactionDateBetween(begin, end)
                .stream()
                .sorted(getExpenseComparator())
                .map(expenseMapper::mapToDto)
                .toList();
    }

    private Comparator<Expense> getExpenseComparator() {
        return (o1, o2) -> {
            if (o1.getCategoryName().equals(StringUtils.EMPTY) && o2.getCategoryName().equals(StringUtils.EMPTY)) {
                return o1.getAmount().compareTo(o2.getAmount());
            }
            if (o1.getCategoryName().equals(StringUtils.EMPTY)) {
                return -1;
            }
            if (o2.getCategoryName().equals(StringUtils.EMPTY)) {
                return 1;
            }
            return o1.getAmount().compareTo(o2.getAmount());
        };
    }

    public Boolean deleteById(Long id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public FireDataDTO getFireNumber() {
        FireDataDTO fireDataDTO = new FireDataDTO();

        List<YearlyExpensesDTO> annualExpensesForPreviousYears = expenseRepository.findAnnualExpensesForPreviousYears();
        if (annualExpensesForPreviousYears.isEmpty()) {
            fireDataDTO.setFireNumber(BigDecimal.ZERO);
            fireDataDTO.setInvestmentSum(BigDecimal.ZERO);
            return fireDataDTO;
        }

        fireDataDTO.setFireNumber(annualExpensesForPreviousYears.stream()
                .map(YearlyExpensesDTO::getExpensesSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(annualExpensesForPreviousYears.size()), RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(25))
                .abs());

        fireDataDTO.setInvestmentSum(expenseRepository
                .findAllByCategory_Level(2)
                .stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .abs());

        return fireDataDTO;
    }
}
