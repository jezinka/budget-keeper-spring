package com.example.budgetkeeperspring.groupedExpenses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("groupedExpenses")
public class GroupedExpensesController {

    @Autowired
    GroupedExpensesRepository groupedExpensesRepository;

    @GetMapping("")
    List<GroupedExpenses> getForCurrentYear() {
        int year = Year.now().getValue();
        return groupedExpensesRepository.getYearAtGlance(year);
    }
}
