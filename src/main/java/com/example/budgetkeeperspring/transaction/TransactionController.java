package com.example.budgetkeeperspring.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
