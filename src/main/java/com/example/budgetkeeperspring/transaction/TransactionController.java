package com.example.budgetkeeperspring.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("transactions")
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionService transactionService;

    @PostMapping("")
    List getAllTransactions(@RequestBody HashMap filters) {
        return transactionRepository.getAllTransactions(filters);
    }

    @GetMapping("/currentMonth")
    List<Transaction> getCurrentMonth() {
        return transactionRepository.findAllForCurrentMonth();
    }

    @GetMapping("/{id}")
    Transaction getById(@PathVariable Long id) {
        return transactionRepository.getById(id);
    }

    @DeleteMapping("/{id}")
    Boolean deleteTransaction(@PathVariable Long id) {
        return transactionRepository.deleteTransaction(id);
    }

    @PutMapping("/{id}")
    Boolean editTransaction(@PathVariable Long id, @RequestBody Transaction updateTransaction) {
        return transactionService.updateTransaction(id, updateTransaction);
    }

    @PostMapping("/split/{id}")
    Boolean splitTransaction(@PathVariable Long id, @RequestBody List<Transaction> updateTransactions) {
        return transactionService.splitTransaction(id, updateTransactions);
    }
}
