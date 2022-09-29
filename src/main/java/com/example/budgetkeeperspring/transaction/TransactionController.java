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
        Transaction transaction = transactionRepository.getById(id);
        if (updateTransaction != null) {
            transaction.setTransactionDate(updateTransaction.getTransactionDate());
            transaction.setTitle(updateTransaction.getTitle());
            transaction.setPayee(updateTransaction.getPayee());
            transaction.setAmount(updateTransaction.getAmount());
            transaction.setCategoryId(updateTransaction.getCategoryId());
        }
        return transactionRepository.editTransaction(transaction);
    }
}
