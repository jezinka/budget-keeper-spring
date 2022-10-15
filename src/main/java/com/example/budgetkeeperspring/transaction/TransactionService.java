package com.example.budgetkeeperspring.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class TransactionService {

    private static final Long EMPTY_OPTION = -1L;
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;


    Boolean updateTransaction(Long id, Transaction updateTransaction) {
        Transaction transaction = transactionRepository.getById(id);
        if (updateTransaction != null) {
            setTransactionProperties(updateTransaction, transaction);
        }
        return transactionRepository.editTransaction(transaction);
    }

    Boolean splitTransaction(Long id, List<Transaction> updateTransactions) {
        Boolean result;
        try {
            result = transactionTemplate.execute(status -> {
                Boolean transResult = true;
                Transaction transaction = transactionRepository.getById(id);
                for (Transaction t : updateTransactions) {
                    setTransactionProperties(t, transaction);
                    transResult = transactionRepository.createTransaction(transaction) && transResult;
                }
                transResult = transactionRepository.deleteTransaction(id) && transResult;
                return transResult;
            });
        } catch (DataIntegrityViolationException exception) {
            return false;
        }
        return result;
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
}
