package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.*;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.CategoryLevel;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.mapper.ExpenseMapper;
import com.example.budgetkeeperspring.repository.CategoryLevelRepository;
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
    public static final String INCOME = "Income";

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryLevelRepository categoryLevelRepository;
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

    public SankeyDto getSankeyData(LocalDate begin, LocalDate end) {
        SankeyDto sankeyDto = new SankeyDto();

        Map<Integer, String> categoryLevels = getCategoryLevels();
        HashMap<String, BigDecimal> categorySums = new HashMap<>();
        HashMap<String, Set<String>> categoriesMap = new HashMap<>();

        List<Expense> validExpenses = getValidExpenses(begin, end);

        validExpenses.stream()
                .filter(expense -> expense.getCategory().getLevel() != 4)
                .forEach(expense -> {
                            String categoryLevel = categoryLevels.getOrDefault(expense.getCategory().getLevel(), "Inne");
                            String categoryName = expense.getCategory().getName();

                            categoriesMap.computeIfAbsent(INCOME, k -> new HashSet<>()).add(categoryLevel);
                            categoriesMap.computeIfAbsent(categoryLevel, k -> new HashSet<>()).add(categoryName);

                            categorySums.merge(categoryName, expense.getAmount(), BigDecimal::add);
                            categorySums.merge(categoryLevel, expense.getAmount(), BigDecimal::add);
                            categorySums.merge(INCOME, expense.getAmount(), BigDecimal::add);
                        }
                );

        Set<SankeyDto.SankeyNode> nodes = new HashSet<>();
        List<SankeyDto.SankeyLink> links = new ArrayList<>();

        if (validExpenses.stream().filter(expense -> expense.getCategory().getLevel() == 4 && expense.getAmount().compareTo(BigDecimal.ZERO) > 0).count() > 1) {
            incomeSource(validExpenses, nodes, links);
        }

        categoriesMap.forEach((sankeyName, sankeyLinks) -> {
            nodes.add(new SankeyDto.SankeyNode(sankeyName));
            if (sankeyLinks.size() > 1) {
                sankeyLinks.forEach(target -> {
                    nodes.add(new SankeyDto.SankeyNode(target));
                    links.add(new SankeyDto.SankeyLink(sankeyName, target, categorySums.getOrDefault(target, BigDecimal.ZERO).abs()));
                });
            } else {
                drilldownForSingleCategory(sankeyName, validExpenses, categoryLevels, links, nodes);
            }
        });

        sankeyDto.setNodes(nodes);
        sankeyDto.setLinks(links);
        return sankeyDto;
    }

    private List<Expense> getValidExpenses(LocalDate begin, LocalDate end) {
        return expenseRepository.findAllByTransactionDateBetween(begin, end).stream()
                .filter(expense -> expense.getCategory() != null)
                .filter(expense -> expense.getCategory().getLevel() != null)
                .filter(expense -> !expense.getCategory().getId().equals(CategoryService.UNKNOWN_CATEGORY))
                .toList();
    }

    private void drilldownForSingleCategory(String sankeyName, List<Expense> validExpenses, Map<Integer, String> categoryLevels, List<SankeyDto.SankeyLink> links, Set<SankeyDto.SankeyNode> nodes) {
        validExpenses.stream()
                .filter(expense -> categoryLevels.get(expense.getCategory().getLevel()).equals(sankeyName))
                .forEach(expense -> {
                    String description = expenseMapper.mapToDto(expense).getDescription();
                    links.add(new SankeyDto.SankeyLink(sankeyName, description, expense.getAmount().abs()));
                    nodes.add(new SankeyDto.SankeyNode(description));
                });
    }

    private void incomeSource(List<Expense> allByTransactionDateBetween, Set<SankeyDto.SankeyNode> nodes, List<SankeyDto.SankeyLink> links) {
        Set<SankeyDto.SankeyNode> incomeNodes = new HashSet<>();
        List<SankeyDto.SankeyLink> incomeLinks = new ArrayList<>();

        allByTransactionDateBetween.stream()
                .filter(expe -> expe.getCategory().getLevel() == 4)
                .collect(groupingBy(Expense::getCategoryName,
                        reducing(BigDecimal.ZERO,
                                Expense::getAmount, BigDecimal::add)))
                .forEach((name, amount) -> {
                    incomeNodes.add(new SankeyDto.SankeyNode(name));
                    incomeLinks.add(new SankeyDto.SankeyLink(name, INCOME, amount));
                });

        nodes.addAll(incomeNodes);
        links.addAll(incomeLinks);
    }

    private Map<Integer, String> getCategoryLevels() {
        return categoryLevelRepository.findAll()
                .stream()
                .collect(toMap(CategoryLevel::getLevel, CategoryLevel::getName));
    }
}
