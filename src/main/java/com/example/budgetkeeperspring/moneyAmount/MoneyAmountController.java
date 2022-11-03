package com.example.budgetkeeperspring.moneyAmount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("moneyAmount")
public class MoneyAmountController {

    @Autowired
    MoneyAmountRepository moneyAmountRepository;

    @GetMapping("")
    MoneyAmount getCurrentMonth() {
        return moneyAmountRepository.findMoneyAmountForCurrentMonth();
    }

    @PutMapping("")
    Boolean addMoneyAmountForCurrentMonth(@RequestBody HashMap newAmount) {
        Float amount = Float.valueOf(newAmount.get("amount").toString());
        return moneyAmountRepository.addMoneyAmountForCurrentMonth(amount);
    }
}
