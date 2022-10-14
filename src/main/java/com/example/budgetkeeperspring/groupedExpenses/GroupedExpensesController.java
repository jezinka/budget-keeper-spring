package com.example.budgetkeeperspring.groupedExpenses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("groupedExpenses")
public class GroupedExpensesController {

    @Autowired
    GroupedExpensesRepository groupedExpensesRepository;

    @GetMapping("/getPivot")
    List getForCurrentYearPivot() {
        int year = Year.now().getValue();
        return groupedExpensesRepository.getMonthsPivot(year);
    }

    @GetMapping("")
    List<GroupedExpenses> getForCurrentYear() {
        int year = Year.now().getValue();
        List<GroupedExpenses> groupedExpenses = new ArrayList<>(groupedExpensesRepository.getYearAtGlance(year));
        groupedExpenses.addAll(groupedExpensesRepository.getCategorySumRows(year));
        groupedExpenses.addAll(groupedExpensesRepository.getMonthSumRows(year));
        return groupedExpenses;
    }

    @GetMapping("/currentMonthByCategory")
    List<GroupedExpenses> getGroupedForCurrentMonth() {
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return groupedExpensesRepository.getGroupedByCategory(begin, end);
    }
}
