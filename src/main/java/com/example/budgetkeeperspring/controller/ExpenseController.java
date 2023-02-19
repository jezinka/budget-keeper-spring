package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.DailyExpensesDTO;
import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.MonthCategoryAmountDTO;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.exception.ExpenseNotFoundException;
import com.example.budgetkeeperspring.mapper.ExpenseMapper;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import com.example.budgetkeeperspring.service.ExpenseService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;

    @PostMapping("")
    List<Expense> getAllExpenses(@RequestBody HashMap<String, Object> filters) {
        return expenseService.findAll(filters);
    }

    @GetMapping("/currentMonth")
    List<ExpenseDTO> getCurrentMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return expenseRepository.findAllByTransactionDateBetween(begin, end).stream().map(expenseMapper::mapToDTO).toList();
    }

    @GetMapping("/{id}")
    ExpenseDTO getById(@PathVariable Long id) {
        return expenseRepository.findById(id).map(expenseMapper::mapToDTO).orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    Boolean deleteExpense(@PathVariable Long id) {
        expenseRepository.deleteById(id);
        return true;
    }

    @PutMapping("/{id}")
    Boolean editExpense(@PathVariable Long id, @RequestBody ExpenseDTO updateExpense) {
        return expenseService.updateTransaction(id, updateExpense);
    }

    @PostMapping("/split/{id}")
    Boolean splitExpense(@PathVariable Long id, @RequestBody List<ExpenseDTO> expenseDTOS) {
        return expenseService.splitExpense(id, expenseDTOS);
    }

    @GetMapping("/getPivot/{year}")
    List<Map<String, Object>> getForSelectedYearPivot(@PathVariable("year") Integer year) {
        return expenseService.getMonthsPivot(year);
    }

    @GetMapping("/yearAtTheGlance/{year}")
    Map<Integer, Map<String, BigDecimal>> getForSelectedYear(@PathVariable("year") Integer year) {
        return expenseService.getYearAtGlance(year);
    }

    @GetMapping("/currentMonthByCategory")
    List<MonthCategoryAmountDTO> getGroupedForCurrentMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return expenseService.getGroupedByCategory(begin, end);
    }

    @GetMapping("/dailyExpenses")
    List<DailyExpensesDTO> getDailyForMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return expenseService.getDailyExpenses(begin, end);
    }
}
