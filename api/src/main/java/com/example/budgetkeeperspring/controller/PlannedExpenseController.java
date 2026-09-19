package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.PlannedExpenseDTO;
import com.example.budgetkeeperspring.dto.RecurringPlannedExpenseDTO;
import com.example.budgetkeeperspring.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/planned-expenses")
@Tag(name = "Planned expenses", description = "Operations for monthly planned expenses")
public class PlannedExpenseController {
    private final PlanService planService;

    @GetMapping("/{id}")
    @Operation(summary = "Get a planned expense by ID")
    PlannedExpenseDTO findById(@PathVariable Integer id) {
        return planService.findPlannedExpense(id);
    }

    @PostMapping
    @Operation(summary = "Create a planned expense")
    ResponseEntity<PlannedExpenseDTO> create(@Valid @RequestBody PlannedExpenseDTO dto) {
        return ResponseEntity.status(201).body(planService.createPlannedExpense(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a planned expense")
    PlannedExpenseDTO update(@PathVariable Integer id, @Valid @RequestBody PlannedExpenseDTO dto) {
        return planService.updatePlannedExpense(id, dto);
    }

    @PatchMapping("/{id}/paid")
    @Operation(summary = "Mark a planned expense as paid or unpaid")
    PlannedExpenseDTO updatePaid(@PathVariable Integer id, @RequestParam boolean paid) {
        return planService.updatePaid(id, paid);
    }

    @PostMapping("/{id}/recurring")
    @Operation(summary = "Convert a planned expense into a recurring payment")
    RecurringPlannedExpenseDTO convertToRecurring(@PathVariable Integer id) {
        return planService.convertPlannedExpenseToRecurring(id);
    }

    @PostMapping("/{plannedExpenseId}/expenses/{expenseId}")
    @Operation(summary = "Assign an expense to a planned expense")
    ResponseEntity<Void> assignExpense(@PathVariable Integer plannedExpenseId, @PathVariable Long expenseId) {
        planService.assignExpenseToPlannedExpense(plannedExpenseId, expenseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{plannedExpenseId}/expenses")
    @Operation(summary = "Assign selected expenses to a planned expense")
    ResponseEntity<Void> assignExpenses(@PathVariable Integer plannedExpenseId, @RequestBody java.util.List<Long> expenseIds) {
        planService.assignExpensesToPlannedExpense(plannedExpenseId, expenseIds);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{plannedExpenseId}/expenses/{expenseId}")
    @Operation(summary = "Unassign an expense from a planned expense")
    ResponseEntity<Void> unassignExpense(@PathVariable Integer plannedExpenseId, @PathVariable Long expenseId) {
        planService.unassignExpenseFromPlannedExpense(plannedExpenseId, expenseId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a planned expense and detach its expenses")
    ResponseEntity<Void> delete(@PathVariable Integer id) {
        planService.deletePlannedExpense(id);
        return ResponseEntity.noContent().build();
    }
}
