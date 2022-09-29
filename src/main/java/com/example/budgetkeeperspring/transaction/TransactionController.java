package com.example.budgetkeeperspring.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("transactions")
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    @GetMapping("")
    List<Transaction> getCurrentMonth() {
        return transactionRepository.findAllForCurrentMonth();
    }

    @DeleteMapping("/{id}")
    Boolean deleteTransaction(@PathVariable Long id) {
        return transactionRepository.deleteTransaction(id);
    }

    @PostMapping("/split/{id}")
    Boolean splitTransaction(@PathVariable Long id, @RequestBody List<Transaction> transaction) {
        return true;
    }
}
