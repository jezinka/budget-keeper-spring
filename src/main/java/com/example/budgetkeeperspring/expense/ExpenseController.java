package com.example.budgetkeeperspring.expense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

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
    List<CurrentMonthExpenses> getCurrentMonth() {
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
}
