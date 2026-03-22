package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.*;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.service.BudgetFlowService;
import com.example.budgetkeeperspring.service.ExpenseService;
import com.example.budgetkeeperspring.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.budgetkeeperspring.utils.DateUtils.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/expenses")
@Tag(name = "Expenses", description = "Operations for managing expenses")
public class ExpenseController {

    public static final String EXPENSES_PATH_ID = "/{id}";
    private final ExpenseService expenseService;
    private final BudgetFlowService budgetFlowService;

    @Operation(summary = "Get expenses with filters")
    @PostMapping()
    List<ExpenseDTO> getAllExpenses(@RequestBody HashMap<String, Object> filters) {
        return expenseService.findAll(filters);
    }

    @Operation(summary = "Create a new expense")
    @PostMapping("/create")
    public ResponseEntity<ExpenseDTO> createExpense(@Validated @RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO created = expenseService.createExpense(expenseDTO);
        // Optionally set Location header - using id if available
        if (created.getId() != null) {
            return ResponseEntity.created(URI.create("/expenses/" + created.getId())).body(created);
        }
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Get expenses for the current month")
    @GetMapping("/currentMonth")
    List<ExpenseDTO> getCurrentMonth() {
        LocalDate begin = getBeginOfCurrentMonth();
        LocalDate end = getEndOfCurrentMonth();
        return expenseService.findAllByTransactionDateBetween(begin, end);
    }

    @Operation(summary = "Get expenses for a selected month")
    @GetMapping("/selectedMonth")
    List<ExpenseDTO> getSelectedMonth(@RequestParam("year") Integer year, @RequestParam("month") Integer month) {
        LocalDate begin = getBeginOfSelectedMonth(year, month);
        LocalDate end = getEndOfSelectedMonth(year, month);
        return expenseService.findAllByTransactionDateBetween(begin, end);
    }

    @Operation(summary = "Get expenses for a selected year")
    @GetMapping("/selectedYear")
    List<ExpenseDTO> getSelectedYear(@RequestParam("year") Integer year) {
        LocalDate begin = DateUtils.getBeginOfSelectedYear(year);
        LocalDate end = DateUtils.getEndOfSelectedYear(year);
        return expenseService.findAllByTransactionDateBetween(begin, end);
    }

    @Operation(summary = "Get an expense by ID")
    @GetMapping(value = EXPENSES_PATH_ID)
    ExpenseDTO getById(@PathVariable Long id) {
        return expenseService.findById(id).orElseThrow(NotFoundException::new);
    }

    @Operation(summary = "Delete an expense by ID")
    @DeleteMapping(EXPENSES_PATH_ID)
    ResponseEntity<ExpenseDTO> deleteExpense(@PathVariable Long id) {
        if (!expenseService.deleteById(id)) {
            throw new NotFoundException();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update an expense by ID")
    @PutMapping(EXPENSES_PATH_ID)
    ResponseEntity<ExpenseDTO> editExpense(@PathVariable Long id, @Validated @RequestBody ExpenseDTO updateExpense) {
        if (expenseService.updateExpense(id, updateExpense).isEmpty()) {
            throw new NotFoundException();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Split an expense into multiple expenses")
    @PostMapping("/split/{id}")
    ResponseEntity<ExpenseDTO> splitExpense(@PathVariable Long id, @Validated @RequestBody List<ExpenseDTO> expenseDTOS) {
        expenseService.splitExpense(id, expenseDTOS);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get year-at-a-glance summary for a selected year")
    @GetMapping("/yearAtTheGlance/{year}")
    List<MonthCategoryAmountDTO> getForSelectedYear(@PathVariable("year") Integer year) {
        return expenseService.getYearAtGlance(year);
    }

    @Operation(summary = "Get current month expenses grouped by category")
    @GetMapping("/currentMonthByCategory")
    List<MonthCategoryAmountDTO> getGroupedForCurrentMonth(@RequestParam(value = "withInvestments", required = false) boolean withInvestments) {
        LocalDate begin = getBeginOfCurrentMonth();
        LocalDate end = getEndOfCurrentMonth();
        return expenseService.getGroupedByCategory(begin, end, withInvestments);
    }

    @Operation(summary = "Get daily expenses for the current month")
    @GetMapping("/dailyExpenses")
    List<DailyExpensesDTO> getDailyForMonth() {
        LocalDate begin = getBeginOfCurrentMonth();
        LocalDate end = getEndOfCurrentMonth();
        return expenseService.getDailyExpenses(begin, end);
    }

    @Operation(summary = "Get monthly budget flow (Sankey chart data)")
    @GetMapping("/monthlyBudgetFlow")
    public SankeyDto getMonthlyBudgetFlow(@RequestParam("year") Integer year, @RequestParam("month") Integer month) {
        LocalDate begin = getBeginOfSelectedMonth(year, month);
        LocalDate end = getEndOfSelectedMonth(year, month);
        return budgetFlowService.getMonthly(begin, end);
    }

    @Operation(summary = "Get yearly budget flow (Sankey chart data)")
    @GetMapping("/yearlyBudgetFlow")
    public SankeyDto getYearlyBudgetFlow(@RequestParam("year") Integer year) {
        LocalDate begin = getBeginOfSelectedYear(year);
        LocalDate end = getEndOfSelectedYear(year);
        return budgetFlowService.getYearly(begin, end);
    }

    @Operation(summary = "Get top expenses for a selected year")
    @GetMapping("/topExpensesForYear")
    List<PieChartExpenseDto> getTopExpensesForYear(@RequestParam("year") Integer year) {
        LocalDate begin = getBeginOfSelectedYear(year);
        LocalDate end = getEndOfSelectedYear(year);
        return expenseService.getTop10ExpensesForTimePeriod(begin, end);
    }

    @Operation(summary = "Get category level expenses for a selected year")
    @GetMapping("/categoryLevelExpensesForYear")
    List<PieChartExpenseDto> categoryLevelExpensesForYear(@RequestParam("year") Integer year) {
        LocalDate begin = getBeginOfSelectedYear(year);
        LocalDate end = getEndOfSelectedYear(year);
        return expenseService.getExpensesPerCategoryLeveBetweenDates(begin, end);
    }

    @Operation(summary = "Get top expenses for a selected month")
    @GetMapping("/topExpensesForMonth")
    List<PieChartExpenseDto> getTopExpensesForYear(@RequestParam("year") Integer year, @RequestParam("month") Integer month) {
        LocalDate begin = getBeginOfSelectedMonth(year, month);
        LocalDate end = getEndOfSelectedMonth(year, month);
        return expenseService.getTop10ExpensesForTimePeriod(begin, end);
    }

    @Operation(summary = "Get category level expenses for a selected month")
    @GetMapping("/categoryLevelExpensesForMonth")
    List<PieChartExpenseDto> categoryLevelExpensesForYear(@RequestParam("year") Integer year, @RequestParam("month") Integer month) {
        LocalDate begin = getBeginOfSelectedMonth(year, month);
        LocalDate end = getEndOfSelectedMonth(year, month);
        return expenseService.getExpensesPerCategoryLeveBetweenDates(begin, end);
    }

    @Operation(summary = "Get investment goal pie chart data for a selected year")
    @GetMapping("/investmentGoalForYear")
    GoalChartDTO investmentGoalForYear(@RequestParam("year") Integer year) {
        LocalDate begin = getBeginOfSelectedYear(year);
        LocalDate end = getEndOfSelectedYear(year);
        return expenseService.getInvestmentGoalPieChartData(begin, end);
    }

    @Operation(summary = "Get yearly expenses grouped by category level")
    @GetMapping("/yearGroupedByLevel")
    Map<String, List<MonthCategoryAmountDTO>> yearGroupedByLevel(@RequestParam("year") Integer year) {
        LocalDate begin = getBeginOfSelectedYear(year);
        LocalDate end = getEndOfSelectedYear(year);
        return expenseService.getYearlyExpensesGroupedByCategoryLevel(begin, end);
    }
}
