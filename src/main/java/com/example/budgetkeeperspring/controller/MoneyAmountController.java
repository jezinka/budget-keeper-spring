package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.CurrentMonthMoneyAmountDTO;
import com.example.budgetkeeperspring.service.MoneyAmountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("moneyAmount")
public class MoneyAmountController {

    private final MoneyAmountService moneyAmountService;

    @GetMapping("")
    CurrentMonthMoneyAmountDTO getCurrentMonth() {
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
