package com.example.budgetkeeperspring.moneyAmount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("moneyAmount")
public class MoneyAmountController {

    @Autowired
    MoneyAmountRepository moneyAmountRepository;

    @GetMapping("")
    MoneyAmount getCurrentMonth() {
        return moneyAmountRepository.findMoneyAmountForCurrentMonth();
    }
}
