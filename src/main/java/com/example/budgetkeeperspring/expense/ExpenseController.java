package com.example.budgetkeeperspring.expense;

import com.example.budgetkeeperspring.YearlyFilter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseRepository expenseRepository, ExpenseService expenseService) {
        this.expenseRepository = expenseRepository;
        this.expenseService = expenseService;
    }

    @PostMapping("")
    List getAllExpenses(@RequestBody HashMap filters) {
        return expenseService.findAll(filters);
    }

    @GetMapping("/currentMonth")
    List<Expense> getCurrentMonth() {
        Date begin = Date.valueOf(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
        Date end = Date.valueOf(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()));
        return expenseRepository.findAllForTimePeriod(begin, end);
    }

    @GetMapping("/{id}")
    Expense getById(@PathVariable Long id) {
        return expenseRepository.getById(id);
    }

    @DeleteMapping("/{id}")
    Boolean deleteExpense(@PathVariable Long id) {
        expenseRepository.deleteById(id);
        return true;
    }

    @PutMapping("/{id}")
    Boolean editExpense(@PathVariable Long id, @RequestBody Expense updateExpense) {
        return expenseService.updateTransaction(id, updateExpense);
    }

    @PostMapping("/split/{id}")
    Boolean splitExpense(@PathVariable Long id, @RequestBody List<Expense> updateExpenses) {
        return expenseService.splitExpense(id, updateExpenses);
    }

    @PostMapping("/getPivot")
    List getForSelectedYearPivot(@RequestBody YearlyFilter filter) {
        return expenseService.getMonthsPivot(filter.getYear());
    }

    @PostMapping("/yearAtTheGlance")
    Map getForSelectedYear(@RequestBody YearlyFilter filter) {
        return expenseService.getYearAtGlance(filter.getYear());
    }

    @GetMapping("/currentMonthByCategory")
    List getGroupedForCurrentMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return expenseService.getGroupedByCategory(Date.valueOf(begin), Date.valueOf(end));
    }

    @GetMapping("/dailyExpenses")
    List getDailyForMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return expenseService.getDailyExpenses(begin, end);
    }
}
