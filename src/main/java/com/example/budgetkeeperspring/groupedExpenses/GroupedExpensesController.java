package com.example.budgetkeeperspring.groupedExpenses;

import com.example.budgetkeeperspring.YearlyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("groupedExpenses")
public class GroupedExpensesController {

    @Autowired
    GroupedExpensesRepository groupedExpensesRepository;

    @PostMapping("/getPivot")
    List getForSelectedYearPivot(@RequestBody YearlyFilter filter) {
        return groupedExpensesRepository.getMonthsPivot(filter.getYear());
    }

    @PostMapping("")
    List<GroupedExpenses> getForSelectedYear(@RequestBody YearlyFilter filter) {
        List<GroupedExpenses> groupedExpenses = new ArrayList<>(groupedExpensesRepository.getYearAtGlance(filter.getYear()));
        groupedExpenses.addAll(groupedExpensesRepository.getCategorySumRows(filter.getYear()));
        groupedExpenses.addAll(groupedExpensesRepository.getMonthSumRows(filter.getYear()));
        return groupedExpenses;
    }

    @GetMapping("/currentMonthByCategory")
    List<GroupedExpenses> getGroupedForCurrentMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return groupedExpensesRepository.getGroupedByCategory(begin, end);
    }

    @GetMapping("/dailyExpenses")
    List getDailyForMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return groupedExpensesRepository.getDailyExpenses(begin, end);
    }
}
