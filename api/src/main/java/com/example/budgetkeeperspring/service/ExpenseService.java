package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.DailyExpensesDTO;
import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.GoalDTO;
import com.example.budgetkeeperspring.dto.MonthCategoryAmountDTO;
import com.example.budgetkeeperspring.dto.YearlyViewDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.mapper.ExpenseMapper;
import com.example.budgetkeeperspring.repository.CategoryRepository;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import com.example.budgetkeeperspring.utils.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExpenseService {

    private static final String CATEGORY = "category";
    private static final String CATEGORY_LEVEL = "categoryLevel";
    private static final String NO_DESCRIPTION = "(brak opisu)";

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseMapper expenseMapper;
    private final GoalService goalService;

    public ExpenseDTO createExpense(ExpenseDTO expenseDTO, Category category) {
        Expense expense = expenseMapper.mapToEntity(expenseDTO);
        expense.setCategory(category);
        return expenseMapper.mapToDto(expenseRepository.save(expense));
    }

    /**
     * Create expense resolving category according to expenseDTO.categoryId:
     * - null: try to find category by conditions (CategoryService.findCategoryByConditions)
     * - -1: treat as no category (null)
     * - >0: load category by id or throw NotFoundException
     */
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        Category category;
        if (expenseDTO.getCategoryId() == null) {
            expenseDTO.setCategoryId(CategoryService.UNKNOWN_CATEGORY);
        }
        category = categoryRepository.findById(expenseDTO.getCategoryId()).orElseThrow(NotFoundException::new);

        return createExpense(expenseDTO, category);
    }

    public Optional<ExpenseDTO> updateExpense(Long id, ExpenseDTO updateExpenseDTO) {
        AtomicReference<Optional<ExpenseDTO>> atomicReference = new AtomicReference<>();

        expenseRepository.findById(id).ifPresentOrElse(foundExpense -> {
            foundExpense.setAmount(updateExpenseDTO.getAmount());
            if (updateExpenseDTO.getNote() != null && !updateExpenseDTO.getNote().isBlank()) {
                foundExpense.setNote(updateExpenseDTO.getNote());
            }
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
            allPredicates.add(p -> p.getCategory().getId() == CategoryService.UNKNOWN_CATEGORY);
        }
        if (filters.getOrDefault("onlyExpenses", false).equals(true)) {
            allPredicates.add(p -> p.getAmount().compareTo(BigDecimal.ZERO) < 0);
        }
        if (filters.get("dateFrom") != null && !filters.get("dateFrom").toString().isEmpty()) {
            LocalDate dateFrom = LocalDate.parse(filters.get("dateFrom").toString());
            allPredicates.add(p -> !p.getTransactionDate().isBefore(dateFrom));
        }
        if (filters.get("dateTo") != null && !filters.get("dateTo").toString().isEmpty()) {
            LocalDate dateTo = LocalDate.parse(filters.get("dateTo").toString());
            allPredicates.add(p -> !p.getTransactionDate().isAfter(dateTo));
        }
        if (filters.get(CATEGORY) != null) {
            allPredicates.add(p -> p.getCategoryName().equals(filters.get(CATEGORY).toString()));
        }
        if (filters.get(CATEGORY_LEVEL) != null) {
            try {
                Integer categoryLevel = Integer.parseInt(filters.get(CATEGORY_LEVEL).toString());
                allPredicates.add(p -> p.getCategory() != null 
                    && p.getCategory().getLevel() != null 
                    && categoryLevel.equals(p.getCategory().getLevel()));
            } catch (NumberFormatException e) {
                // If the categoryLevel filter is not a valid integer, ignore it
            }
        }
        if (!filters.getOrDefault("description", "").equals("")) {
            String searchTerm = filters.get("description").toString().toLowerCase();
            allPredicates.add(p -> {
                boolean matchesTitle = p.getTitle() != null && p.getTitle().toLowerCase().contains(searchTerm);
                boolean matchesPayee = p.getPayee() != null && p.getPayee().toLowerCase().contains(searchTerm);
                boolean matchesNote = p.getNote() != null && p.getNote().toLowerCase().contains(searchTerm);
                return matchesTitle || matchesPayee || matchesNote;
            });
        }
        if (!filters.getOrDefault("amount", "").equals("")) {
            try {
                BigDecimal filterAmount = new BigDecimal(filters.get("amount").toString());
                allPredicates.add(p -> p.getAmount().compareTo(filterAmount) == 0);
            } catch (NumberFormatException e) {
                // If the amount filter is not a valid number, ignore it
            }
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

    public List<MonthCategoryAmountDTO> getGroupedByCategory(LocalDate begin, LocalDate end,
                                                             boolean withInvestments) {
        List<MonthCategoryAmountDTO> list = new ArrayList<>();
        List<Expense> yearlyExpenses;
        if (withInvestments) {
            yearlyExpenses = expenseRepository.findAllByTransactionDateBetween(begin, end);
        } else {
            yearlyExpenses = expenseRepository.findAllByTransactionDateBetweenWithoutInvestments(begin, end);
        }

        yearlyExpenses
                .stream()
                .collect(groupingBy(Expense::getTransactionMonth,
                        groupingBy(Expense::getCategoryName,
                                reducing(BigDecimal.ZERO,
                                        Expense::getAmount, BigDecimal::add))))
                .forEach((month, value) ->
                        value.forEach((category, amount) -> {
                                    if (amount.compareTo(BigDecimal.ZERO) < 0)
                                        list.add(new MonthCategoryAmountDTO(month, category, amount.abs()));
                                }
                        ));
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
            if (o1.getCategory().getId().equals(CategoryService.UNKNOWN_CATEGORY) && o2.getCategory().getId().equals(CategoryService.UNKNOWN_CATEGORY)) {
                return o1.getAmount().compareTo(o2.getAmount());
            }
            if (o1.getCategory().getId().equals(CategoryService.UNKNOWN_CATEGORY)) {
                return -1;
            }
            if (o2.getCategory().getId().equals(CategoryService.UNKNOWN_CATEGORY)) {
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

    public ObjectNode getLifestyleInflation() {
        Map<String, Map<Integer, BigDecimal>> expensesSumForYear = expenseRepository.findAllByOrderByTransactionDateDesc()
                .stream()
                .filter(exp -> !exp.getCategory().getId().equals(CategoryService.UNKNOWN_CATEGORY))
                .filter(exp -> exp.getCategory().getLevel() != null && !List.of(2, 4, 5).contains(exp.getCategory().getLevel()))
                .collect(groupingBy(
                                Expense::getCategoryName,
                                groupingBy(Expense::getTransactionYear,
                                        reducing(BigDecimal.ZERO,
                                                Expense::getAmount, BigDecimal::add))
                        )
                );

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        expensesSumForYear.forEach((k, v) -> {
            if (v.values().stream().allMatch(amount -> amount.compareTo(BigDecimal.ZERO) == 0)) {
                return;
            }

            if (v.containsKey(LocalDate.now().getYear() - 1) || v.containsKey(LocalDate.now().getYear())) {
                ObjectNode node = mapper.createObjectNode();
                if (v.size() > 1) {
                    node.put("category", k);
                    v.forEach((year, amount) -> node.put(year.toString(), amount.abs()));
                    arrayNode.add(node);
                }
            }
        });

        ObjectNode data = mapper.createObjectNode();
        data.set("data", arrayNode);
        return data;
    }

    public YearlyViewDTO getYearlyViewData(int year) {
        LocalDate begin = DateUtils.getBeginOfSelectedYear(year);
        LocalDate end = DateUtils.getEndOfSelectedYear(year);

        List<Expense> yearlyExpenses = expenseRepository.findAllByTransactionDateBetween(begin, end);

        // Separate expenses and incomes
        List<Expense> expenses = yearlyExpenses.stream()
                .filter(t -> t.getCategory() != null && (t.getCategory().getLevel() == null || t.getCategory().getLevel() != 4))
                .toList();
        List<Expense> incomes = yearlyExpenses.stream()
                .filter(t -> t.getCategory() != null && t.getCategory().getLevel() != null && t.getCategory().getLevel() == 4)
                .toList();

        // Fetch all categories once to avoid N+1 query problem
        List<Category> allCategories = categoryRepository.findAll();
        Map<Integer, String> levelToNameMap = allCategories.stream()
                .filter(c -> c.getLevel() != null)
                .collect(toMap(
                        Category::getLevel,
                        Category::getName,
                        (existing, replacement) -> existing  // Keep first if duplicates
                ));

        // Get all unique category levels from expenses (excluding level 4)
        Map<Integer, String> levelNames = expenses.stream()
                .filter(e -> e.getCategory() != null && e.getCategory().getLevel() != null)
                .collect(groupingBy(e -> e.getCategory().getLevel()))
                .keySet().stream()
                .collect(toMap(
                        level -> level,
                        level -> levelToNameMap.getOrDefault(level, "Level " + level)
                ));

        // Calculate monthly sums per category level
        List<YearlyViewDTO.CategoryLevelSummary> categoryLevelSummaries = levelNames.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Integer level = entry.getKey();
                    String name = entry.getValue();

                    // Calculate monthly sums and transaction counts for this level
                    Map<Integer, BigDecimal> monthlySums = new HashMap<>();
                    Map<Integer, Long> monthlyTransactionCounts = new HashMap<>();
                    for (int month = 1; month <= 12; month++) {
                        monthlySums.put(month, BigDecimal.ZERO);
                        monthlyTransactionCounts.put(month, 0L);
                    }

                    expenses.stream()
                            .filter(e -> e.getCategory() != null && level.equals(e.getCategory().getLevel()))
                            .forEach(e -> {
                                int month = e.getTransactionMonth();
                                BigDecimal currentSum = monthlySums.getOrDefault(month, BigDecimal.ZERO);
                                monthlySums.put(month, currentSum.add(e.getAmount()));
                                Long currentCount = monthlyTransactionCounts.getOrDefault(month, 0L);
                                monthlyTransactionCounts.put(month, currentCount + 1);
                            });

                    // Calculate total for this level
                    BigDecimal totalSum = monthlySums.values().stream()
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return YearlyViewDTO.CategoryLevelSummary.builder()
                            .level(level)
                            .name(name)
                            .monthlySums(monthlySums)
                            .monthlyTransactionCounts(monthlyTransactionCounts)
                            .totalSum(totalSum)
                            .build();
                })
                .filter(summary -> summary.getTotalSum().compareTo(BigDecimal.ZERO) != 0) // Filter out zero totals
                .toList();

        // Calculate monthly income sums and transaction counts (level 4)
        List<BigDecimal> monthlyIncomeSums = new ArrayList<>();
        List<Long> monthlyIncomeTransactionCounts = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            final int m = month;
            BigDecimal monthSum = incomes.stream()
                    .filter(t -> t.getTransactionMonth() == m)
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            monthlyIncomeSums.add(monthSum);
            
            long monthCount = incomes.stream()
                    .filter(t -> t.getTransactionMonth() == m)
                    .count();
            monthlyIncomeTransactionCounts.add(monthCount);
        }
        BigDecimal totalIncomeYear = monthlyIncomeSums.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Prepare pie chart data (expenses by level, excluding level 4)
        Map<Integer, BigDecimal> levelTotals = expenses.stream()
                .filter(e -> e.getCategory() != null && e.getCategory().getLevel() != null)
                .collect(groupingBy(
                        e -> e.getCategory().getLevel(),
                        reducing(BigDecimal.ZERO, e -> e.getAmount().abs(), BigDecimal::add)
                ));

        List<YearlyViewDTO.PieChartData> expensePieData = levelTotals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> YearlyViewDTO.PieChartData.builder()
                        .level(entry.getKey())
                        .name(levelNames.getOrDefault(entry.getKey(), "Level " + entry.getKey()))
                        .value(entry.getValue())
                        .build())
                .toList();

        // Calculate top expenses (distinct by description, excluding levels 2 and 4)
        Map<String, BigDecimal> expensesByDescription = expenses.stream()
                .filter(t -> t.getCategory() != null && t.getCategory().getLevel() != null)
                .filter(t -> t.getCategory().getLevel() != 2 && t.getCategory().getLevel() != 4)
                .collect(groupingBy(
                        t -> t.getTitle() != null && !t.getTitle().isBlank() ? t.getTitle().trim() : NO_DESCRIPTION,
                        reducing(BigDecimal.ZERO, t -> t.getAmount().abs(), BigDecimal::add)
                ));

        List<YearlyViewDTO.TopExpenseData> topExpenses = expensesByDescription.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    String fullDesc = entry.getKey();
                    String truncatedName = fullDesc.length() > 30 
                            ? fullDesc.substring(0, 30) + "..." 
                            : fullDesc;
                    return YearlyViewDTO.TopExpenseData.builder()
                            .name(truncatedName)
                            .fullDescription(fullDesc)
                            .value(entry.getValue())
                            .build();
                })
                .toList();

        return YearlyViewDTO.builder()
                .year(year)
                .categoryLevels(categoryLevelSummaries)
                .monthlyIncomeSums(monthlyIncomeSums)
                .monthlyIncomeTransactionCounts(monthlyIncomeTransactionCounts)
                .totalIncomeYear(totalIncomeYear)
                .expensePieData(expensePieData)
                .topExpenses(topExpenses)
                .build();
    }
}
