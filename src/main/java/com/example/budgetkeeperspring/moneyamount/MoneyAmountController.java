package com.example.budgetkeeperspring.moneyamount;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

@RestController
@RequestMapping("moneyAmount")
public class MoneyAmountController {

    private final MoneyAmountService moneyAmountService;

    public MoneyAmountController(MoneyAmountService moneyAmountService) {
        this.moneyAmountService = moneyAmountService;
    }

    @GetMapping("")
    CurrentMonthMoneyAmount getCurrentMonth() {
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return moneyAmountService.getForPeriod(startDate, endDate);
    }

    @PutMapping("")
    Boolean addMoneyAmountForCurrentMonth(@RequestBody Map<String, String> newAmount) {
        if (newAmount.containsKey("amount")) {
            return moneyAmountService.addMoneyAmountForCurrentMonth(newAmount);
        }
        return false;
    }
}