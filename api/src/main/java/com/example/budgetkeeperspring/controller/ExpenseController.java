package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.DailyExpensesDTO;
import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.MonthCategoryAmountDTO;
import com.example.budgetkeeperspring.dto.SankeyDto;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.service.BudgetFlowService;
import com.example.budgetkeeperspring.service.ExpenseService;
import com.example.budgetkeeperspring.utils.DateUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static com.example.budgetkeeperspring.utils.DateUtils.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    public static final String EXPENSES_PATH_ID = "/{id}";
    private final ExpenseService expenseService;
    private final BudgetFlowService budgetFlowService;

    @PostMapping()
    List<ExpenseDTO> getAllExpenses(@RequestBody HashMap<String, Object> filters) {
        return expenseService.findAll(filters);
    }

    @PostMapping("/create")
    public ResponseEntity<ExpenseDTO> createExpense(@Validated @RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO created = expenseService.createExpense(expenseDTO);
        // Optionally set Location header - using id if available
        if (created.getId() != null) {
            return ResponseEntity.created(URI.create("/expenses/" + created.getId())).body(created);
        }
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/currentMonth")
    List<ExpenseDTO> getCurrentMonth() {
        LocalDate begin = getBeginOfCurrentMonth();
        LocalDate end = getEndOfCurrentMonth();
        return expenseService.findAllByTransactionDateBetween(begin, end);
    }

    @GetMapping("/selectedMonth")
    List<ExpenseDTO> getSelectedMonth(@RequestParam("year") Integer year, @RequestParam("month") Integer month) {
        LocalDate begin = getBeginOfSelectedMonth(year, month);
        LocalDate end = getEndOfSelectedMonth(year, month);
        return expenseService.findAllByTransactionDateBetween(begin, end);
    }

    @GetMapping("/selectedYear")
    List<ExpenseDTO> getSelectedYear(@RequestParam("year") Integer year) {
        LocalDate begin = DateUtils.getBeginOfSelectedYear(year);
        LocalDate end = DateUtils.getEndOfSelectedYear(year);
        return expenseService.findAllByTransactionDateBetween(begin, end);
    }

    @GetMapping(value = EXPENSES_PATH_ID)
    ExpenseDTO getById(@PathVariable Long id) {
        return expenseService.findById(id).orElseThrow(NotFoundException::new);
    }

    @DeleteMapping(EXPENSES_PATH_ID)
    ResponseEntity<ExpenseDTO> deleteExpense(@PathVariable Long id) {
        if (!expenseService.deleteById(id)) {
            throw new NotFoundException();
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(EXPENSES_PATH_ID)
    ResponseEntity<ExpenseDTO> editExpense(@PathVariable Long id, @Validated @RequestBody ExpenseDTO updateExpense) {
        if (expenseService.updateExpense(id, updateExpense).isEmpty()) {
            throw new NotFoundException();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/split/{id}")
    ResponseEntity<ExpenseDTO> splitExpense(@PathVariable Long id, @Validated @RequestBody List<ExpenseDTO> expenseDTOS) {
        expenseService.splitExpense(id, expenseDTOS);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/yearAtTheGlance/{year}")
    List<MonthCategoryAmountDTO> getForSelectedYear(@PathVariable("year") Integer year) {
        return expenseService.getYearAtGlance(year);
    }

    @GetMapping("/currentMonthByCategory")
    List<MonthCategoryAmountDTO> getGroupedForCurrentMonth(@RequestParam(value = "withInvestments", required = false) boolean withInvestments) {
        LocalDate begin = getBeginOfCurrentMonth();
        LocalDate end = getEndOfCurrentMonth();
        return expenseService.getGroupedByCategory(begin, end, withInvestments);
    }

    @GetMapping("/dailyExpenses")
    List<DailyExpensesDTO> getDailyForMonth() {
        LocalDate begin = getBeginOfCurrentMonth();
        LocalDate end = getEndOfCurrentMonth();
        return expenseService.getDailyExpenses(begin, end);
    }

    @GetMapping("/lifestyleInflation")
    public ObjectNode getLifestyleInflation() {
        return expenseService.getLifestyleInflation();
    }

    @GetMapping("/monthlyBudgetFlow")
    public SankeyDto getMonthlyBudgetFlow(@RequestParam("year") Integer year, @RequestParam("month") Integer month) {
        LocalDate begin = getBeginOfSelectedMonth(year, month);
        LocalDate end = getEndOfSelectedMonth(year, month);
        return budgetFlowService.getMonthly(begin, end);
    }

    @GetMapping("/yearlyBudgetFlow")
    public SankeyDto getYearlyBudgetFlow(@RequestParam("year") Integer year) {
        LocalDate begin = getBeginOfSelectedYear(year);
        LocalDate end = getEndOfSelectedYear(year);
        return budgetFlowService.getYearly(begin, end);
    }
}
