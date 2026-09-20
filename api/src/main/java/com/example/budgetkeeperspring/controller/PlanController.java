package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.PlanDTO;
import com.example.budgetkeeperspring.dto.PlannedExpenseDTO;
import com.example.budgetkeeperspring.dto.RecurringPlannedExpenseDTO;
import com.example.budgetkeeperspring.dto.PlanSummaryDTO;
import com.example.budgetkeeperspring.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/plans")
@Tag(name = "Plans", description = "Monthly expense plans")
public class PlanController {
    private final PlanService planService;

    @GetMapping
    @Operation(summary = "Get all plans")
    List<PlanDTO> findAll() {
        return planService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a plan by ID")
    PlanDTO findById(@PathVariable Integer id) {
        return planService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create a plan for a selected month")
    ResponseEntity<PlanDTO> create(@Valid @RequestBody PlanDTO dto) {
        return ResponseEntity.status(201).body(planService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a plan month")
    PlanDTO update(@PathVariable Integer id, @Valid @RequestBody PlanDTO dto) {
        return planService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a plan and detach its expenses")
    ResponseEntity<Void> delete(@PathVariable Integer id) {
        planService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{planId}/plannedExpenses")
    @Operation(summary = "Get planned expenses for a plan")
    List<PlannedExpenseDTO> findPlannedExpenses(@PathVariable Integer planId) {
        return planService.findPlannedExpenses(planId);
    }

    @GetMapping("/{planId}/expenses")
    @Operation(summary = "Get all expenses linked to planned expenses in a plan")
    List<ExpenseDTO> findExpensesForPlan(@PathVariable Integer planId) {
        return planService.findExpensesForPlan(planId);
    }

    @PostMapping("/{planId}/plannedExpenses/fromExpense/{expenseId}")
    @Operation(summary = "Mark an expense as planned and create its planned expense")
    PlannedExpenseDTO markExpenseAsPlanned(@PathVariable Integer planId, @PathVariable Long expenseId) {
        return planService.markExpenseAsPlanned(planId, expenseId);
    }

    @PostMapping("/{planId}/plannedExpenses/fromExpenses")
    @Operation(summary = "Create a planned expense from selected expenses")
    PlannedExpenseDTO createPlannedExpenseFromExpenses(@PathVariable Integer planId, @RequestBody List<Long> expenseIds) {
        return planService.createPlannedExpenseFromExpenses(planId, expenseIds);
    }

    @PostMapping("/{planId}/recurringPlannedExpenses")
    @Operation(summary = "Apply active recurring planned expenses to a plan")
    ResponseEntity<Void> applyRecurringPayments(@PathVariable Integer planId) {
        planService.applyRecurringPayments(planId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{planId}/unplannedExpenses/{expenseId}/recurring")
    @Operation(summary = "Convert an unplanned expense into a recurring payment")
    RecurringPlannedExpenseDTO convertUnplannedExpenseToRecurring(@PathVariable Integer planId, @PathVariable Long expenseId) {
        return planService.convertUnplannedExpenseToRecurring(planId, expenseId);
    }

    @PostMapping("/{planId}/upload")
    @Operation(summary = "Import planned expenses from a Kategoria,Kwota CSV file")
    ResponseEntity<Void> importPlannedExpenses(@PathVariable Integer planId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        planService.importPlannedExpenses(planId, file);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unplannedExpenses")
    @Operation(summary = "Get expenses outside a plan for a selected month")
    List<ExpenseDTO> findUnplannedExpenses(@RequestParam @Min(1) int year, @RequestParam @Min(1) @Max(12) int month) {
        return planService.findUnplannedExpenses(year, month);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get planned and unplanned monthly expenses, pie chart and category statistics")
    PlanSummaryDTO summary(@RequestParam @Min(1) int year, @RequestParam @Min(1) @Max(12) int month) {
        return planService.summary(year, month);
    }
}
