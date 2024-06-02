package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.BudgetPlanDTO;
import com.example.budgetkeeperspring.dto.BudgetPlanSummaryDTO;
import com.example.budgetkeeperspring.service.BudgetPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static com.example.budgetkeeperspring.service.DateUtilsService.getBeginOfCurrentMonth;
import static com.example.budgetkeeperspring.service.DateUtilsService.getEndOfCurrentMonth;

@RestController
@RequestMapping("/budgetPlan")
public class BudgetPlanController {

    private final BudgetPlanService budgetPlanService;

    @Autowired
    public BudgetPlanController(BudgetPlanService budgetPlanService) {
        this.budgetPlanService = budgetPlanService;
    }

    @GetMapping
    public List<BudgetPlanDTO> getBudgetPlan() {
        LocalDate startDate = getBeginOfCurrentMonth();
        LocalDate endDate = getEndOfCurrentMonth();
        return budgetPlanService.getBudgetPlan(startDate, endDate);
    }

    @GetMapping("/summary")
    public BudgetPlanSummaryDTO getBudgetPlanSummary() {
        LocalDate startDate = getBeginOfCurrentMonth();
        LocalDate endDate = getEndOfCurrentMonth();
        return budgetPlanService.getBudgetPlanSummary(startDate, endDate);
    }
}