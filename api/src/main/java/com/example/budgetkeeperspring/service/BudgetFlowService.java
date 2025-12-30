package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.SankeyDto;
import com.example.budgetkeeperspring.entity.CategoryLevel;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.mapper.ExpenseMapper;
import com.example.budgetkeeperspring.repository.CategoryLevelRepository;
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
    private final CategoryLevelRepository categoryLevelRepository;
    private final ExpenseMapper expenseMapper;

    public SankeyDto getMonthly(LocalDate begin, LocalDate end) {
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
