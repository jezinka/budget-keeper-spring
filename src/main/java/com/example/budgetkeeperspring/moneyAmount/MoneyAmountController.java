package com.example.budgetkeeperspring.moneyAmount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("moneyAmount")
public class MoneyAmountController {

    @Autowired
    MoneyAmountRepository moneyAmountRepository;

    @Autowired
    CurrentMonthMoneyAmountRepository currentMonthMoneyAmountRepository;

    @GetMapping("")
    CurrentMonthMoneyAmount getCurrentMonth() {
        List<CurrentMonthMoneyAmount> currentMonthMoneyAmounts = currentMonthMoneyAmountRepository.findAll();
        if (currentMonthMoneyAmounts.size() != 1) {
            return null;
        }
        return currentMonthMoneyAmounts.get(0);
    }

    @PutMapping("")
    Boolean addMoneyAmountForCurrentMonth(@RequestBody HashMap newAmount) {
        Float amount = Float.valueOf(newAmount.get("amount").toString());
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        MoneyAmount moneyAmount = new MoneyAmount(begin, amount);
        moneyAmountRepository.save(moneyAmount);
        return true;
    }
}
