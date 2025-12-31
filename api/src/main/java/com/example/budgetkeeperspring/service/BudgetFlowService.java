package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.SankeyDto;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.mapper.ExpenseMapper;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class BudgetFlowService {

    private static final String INCOME = "Income";

    private final ExpenseRepository expenseRepository;
    private final CategoryLevelService categoryLevelService;
    private final ExpenseMapper expenseMapper;

    public SankeyDto getYearly(LocalDate begin, LocalDate end) {

        Map<Integer, String> categoryLevels = categoryLevelService.getCategoryLevels();
        HashMap<String, BigDecimal> categorySums = new HashMap<>();
        HashMap<String, Set<String>> flow = new HashMap<>();

        Result sortedExpenses = getSortedExpenses(begin, end);

        sortedExpenses.validExpenses().forEach(expense -> {
                    String categoryLevel = categoryLevels.getOrDefault(expense.getCategory().getLevel(), "Inne");

                    flow.computeIfAbsent(INCOME, k -> new HashSet<>()).add(categoryLevel);

                    categorySums.merge(categoryLevel, expense.getAmount(), BigDecimal::add);
                    categorySums.merge(INCOME, expense.getAmount(), BigDecimal::add);
                }
        );

        SankeyDto sankeyDto = new SankeyDto();
        incomeSource(sortedExpenses.validIncomes(), sankeyDto);

        flow.forEach((sankeyName, sankeyLinks) -> {
            sankeyDto.getNodes().add(new SankeyDto.SankeyNode(sankeyName));
            sankeyLinks.forEach(target -> {
                sankeyDto.getNodes().add(new SankeyDto.SankeyNode(target));
                sankeyDto.getLinks().add(new SankeyDto.SankeyLink(sankeyName, target, categorySums.getOrDefault(target, BigDecimal.ZERO).abs()));
            });
        });
        return sankeyDto;
    }

    public SankeyDto getMonthly(LocalDate begin, LocalDate end) {

        Map<Integer, String> categoryLevels = categoryLevelService.getCategoryLevels();
        HashMap<String, BigDecimal> categorySums = new HashMap<>();
        HashMap<String, Set<String>> flow = new HashMap<>();

        Result sortedExpenses = getSortedExpenses(begin, end);

        sortedExpenses.validExpenses().forEach(expense -> {
                    String categoryLevel = categoryLevels.getOrDefault(expense.getCategory().getLevel(), "Inne");
                    String categoryName = expense.getCategory().getName();

                    flow.computeIfAbsent(INCOME, k -> new HashSet<>()).add(categoryLevel);
                    flow.computeIfAbsent(categoryLevel, k -> new HashSet<>()).add(categoryName);

                    categorySums.merge(categoryName, expense.getAmount(), BigDecimal::add);
                    categorySums.merge(categoryLevel, expense.getAmount(), BigDecimal::add);
                    categorySums.merge(INCOME, expense.getAmount(), BigDecimal::add);
                }
        );

        SankeyDto sankeyDto = new SankeyDto();

        if (sortedExpenses.validIncomes().size() > 1) {
            incomeSource(sortedExpenses.validIncomes(), sankeyDto);
        }

        flow.forEach((sankeyName, sankeyLinks) -> {
            sankeyDto.getNodes().add(new SankeyDto.SankeyNode(sankeyName));
            if (sankeyLinks.size() > 1) {
                sankeyLinks.forEach(target -> {
                    sankeyDto.getNodes().add(new SankeyDto.SankeyNode(target));
                    sankeyDto.getLinks().add(new SankeyDto.SankeyLink(sankeyName, target, categorySums.getOrDefault(target, BigDecimal.ZERO).abs()));
                });
            } else {
                drilldownForSingleCategory(sankeyName, sortedExpenses.validExpenses(), categoryLevels, sankeyDto);
            }
        });
        return sankeyDto;
    }

    private Result getSortedExpenses(LocalDate begin, LocalDate end) {
        List<Expense> validTransactions = validTransactions(begin, end);
        List<Expense> validIncomes = getValidIncomes(validTransactions);

        List<Expense> validExpenses = new ArrayList<>(validTransactions);
        validExpenses.removeAll(validIncomes);
        return new Result(validIncomes, validExpenses);
    }

    private record Result(List<Expense> validIncomes, List<Expense> validExpenses) {
    }

    private List<Expense> getValidIncomes(List<Expense> validTransactions) {
        return validTransactions.stream()
                .filter(expense -> (expense.getCategory().getLevel() == 4 && expense.getAmount().compareTo(BigDecimal.ZERO) > 0)
                        || (expense.getCategory().getLevel() == 2 && expense.getAmount().compareTo(BigDecimal.ZERO) > 0))
                .toList();
    }

    private List<Expense> validTransactions(LocalDate begin, LocalDate end) {
        return expenseRepository.findAllByTransactionDateBetween(begin, end).stream()
                .filter(expense -> expense.getCategory() != null)
                .filter(expense -> expense.getCategory().getLevel() != null)
                .filter(expense -> !expense.getCategory().getId().equals(CategoryService.UNKNOWN_CATEGORY))
                .toList();
    }

    private void drilldownForSingleCategory(String sankeyName, List<Expense> validExpenses, Map<Integer, String> categoryLevels, SankeyDto sankeyDto) {
        validExpenses.stream()
                .filter(expense -> categoryLevels.get(expense.getCategory().getLevel()).equals(sankeyName))
                .forEach(expense -> {
                    String description = expenseMapper.mapToDto(expense).getDescription();
                    sankeyDto.getLinks().add(new SankeyDto.SankeyLink(sankeyName, description, expense.getAmount().abs()));
                    sankeyDto.getNodes().add(new SankeyDto.SankeyNode(description));
                });
    }

    private void incomeSource(List<Expense> validIncomes, SankeyDto sankeyDto) {
        validIncomes.stream()
                .collect(groupingBy(Expense::getCategoryName,
                        reducing(BigDecimal.ZERO,
                                Expense::getAmount, BigDecimal::add)))
                .forEach((name, amount) -> {
                    sankeyDto.getNodes().add(new SankeyDto.SankeyNode(name));
                    sankeyDto.getLinks().add(new SankeyDto.SankeyLink(name, INCOME, amount));
                });
    }
}
