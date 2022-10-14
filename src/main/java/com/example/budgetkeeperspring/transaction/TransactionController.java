package com.example.budgetkeeperspring.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("transactions")
public class TransactionController {

    private static final Long EMPTY_OPTION = -1L;
    @Autowired
    TransactionRepository transactionRepository;

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
        Transaction transaction = transactionRepository.getById(id);
        if (updateTransaction != null) {
            setTransactionProperties(updateTransaction, transaction);
        }
        return transactionRepository.editTransaction(transaction);
    }

    private static void setTransactionProperties(Transaction updateTransaction, Transaction transaction) {
        transaction.setTransactionDate(updateTransaction.getTransactionDate());
        transaction.setTitle(updateTransaction.getTitle());
        transaction.setPayee(updateTransaction.getPayee());
        transaction.setAmount(updateTransaction.getAmount());
        if (updateTransaction.getCategoryId() != EMPTY_OPTION) {
            transaction.setCategoryId(updateTransaction.getCategoryId());
        }
        if (updateTransaction.getLiabilityId() != EMPTY_OPTION) {
            transaction.setLiabilityId(updateTransaction.getLiabilityId());
        }
    }

    @PostMapping("/split/{id}")
    Boolean splitTransaction(@PathVariable Long id, @RequestBody List<Transaction> updateTransactions) {
        Boolean result = true;
        Transaction transaction = transactionRepository.getById(id);
        for (Transaction t : updateTransactions) {
            setTransactionProperties(t, transaction);
            result = transactionRepository.createTransaction(transaction) && result;
        }
        result = transactionRepository.deleteTransaction(id) && result;
        return result;
    }
}
