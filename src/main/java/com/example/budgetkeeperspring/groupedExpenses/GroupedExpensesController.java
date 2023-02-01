package com.example.budgetkeeperspring.groupedExpenses;

import com.example.budgetkeeperspring.YearlyFilter;
import com.example.budgetkeeperspring.expense.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

@RestController
@RequestMapping("groupedExpenses")
public class GroupedExpensesController {

    @Autowired
    ExpenseService expenseService;

    @Autowired
    ExpensesCategoryService expensesCategoryService;

    @PostMapping("/getPivot")
    Map getForSelectedYearPivot(@RequestBody YearlyFilter filter) {
        return expensesCategoryService.getMonthsPivot(filter.getYear());
    }

    @PostMapping("")
    Map getForSelectedYear(@RequestBody YearlyFilter filter) {
        return expensesCategoryService.getYearAtGlance(filter.getYear());
    }

    @GetMapping("/currentMonthByCategory")
    Map getGroupedForCurrentMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return expensesCategoryService.getGroupedByCategory(Date.valueOf(begin), Date.valueOf(end));
    }

    @GetMapping("/dailyExpenses")
    Map getDailyForMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return expenseService.getDailyExpenses(begin, end);
    }
}
