package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.RecurringPlannedExpenseDTO;
import com.example.budgetkeeperspring.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recurring-planned-expenses")
@Tag(name = "Recurring planned expenses", description = "Monthly recurring payment templates")
public class RecurringPlannedExpenseController {
    private final PlanService planService;

    @GetMapping
    @Operation(summary = "Get recurring planned expenses")
    List<RecurringPlannedExpenseDTO> findAll() {
        return planService.findRecurringPayments();
    }

    @PostMapping
    @Operation(summary = "Create a recurring planned expense")
    ResponseEntity<RecurringPlannedExpenseDTO> create(@Valid @RequestBody RecurringPlannedExpenseDTO dto) {
        return ResponseEntity.status(201).body(planService.createRecurringPayment(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a recurring planned expense")
    RecurringPlannedExpenseDTO update(@PathVariable Integer id, @Valid @RequestBody RecurringPlannedExpenseDTO dto) {
        return planService.updateRecurringPayment(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a recurring planned expense")
    ResponseEntity<Void> delete(@PathVariable Integer id) {
        planService.deleteRecurringPayment(id);
        return ResponseEntity.noContent().build();
    }
}
