package com.example.budgetkeeperspring.expense;

import com.example.budgetkeeperspring.YearlyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("expenses")
public class ExpenseController {

    @Autowired
    ExpenseRepository expenseRepository;

    @Autowired
    ExpenseService expenseService;

    @PostMapping("")
    List getAllExpanses(@RequestBody HashMap filters) {
        return expenseService.findAll(filters);
    }

    @GetMapping("/currentMonth")
    List<Expense> getCurrentMonth() {
        return expenseRepository.findAllForCurrentMonth();
    }

    @GetMapping("/{id}")
    Expense getById(@PathVariable Long id) {
        return expenseRepository.getById(id);
    }

    @DeleteMapping("/{id}")
    Boolean deleteExpanse(@PathVariable Long id) {
        expenseRepository.deleteById(id);
        return true;
    }

    @PutMapping("/{id}")
    Boolean editExpanse(@PathVariable Long id, @RequestBody Expense updateExpense) {
        return expenseService.updateTransaction(id, updateExpense);
    }

    @PostMapping("/split/{id}")
    Boolean splitExpanse(@PathVariable Long id, @RequestBody List<Expense> updateExpenses) {
        return expenseService.splitExpanse(id, updateExpenses);
    }

    @PostMapping("/getPivot")
    Map getForSelectedYearPivot(@RequestBody YearlyFilter filter) {
        return expenseService.getMonthsPivot(filter.getYear());
    }

    @PostMapping("/yearAtTheGlance")
    Map getForSelectedYear(@RequestBody YearlyFilter filter) {
        return expenseService.getYearAtGlance(filter.getYear());
    }

    @GetMapping("/currentMonthByCategory")
    Map getGroupedForCurrentMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return expenseService.getGroupedByCategory(Date.valueOf(begin), Date.valueOf(end));
    }

    @GetMapping("/dailyExpenses")
    Map getDailyForMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return expenseService.getDailyExpenses(begin, end);
    }
}
