package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.BudgetPlanDTO;
import com.example.budgetkeeperspring.dto.BudgetPlanSummaryDTO;
import com.example.budgetkeeperspring.service.BudgetPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static com.example.budgetkeeperspring.utils.DateUtils.getBeginOfCurrentMonth;
import static com.example.budgetkeeperspring.utils.DateUtils.getEndOfCurrentMonth;

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

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload.");
        }

        boolean success = budgetPlanService.createGoalsFromFile(file);
        if (!success) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File uploaded failed.");
        }

        return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully: " + file.getOriginalFilename());
    }
}