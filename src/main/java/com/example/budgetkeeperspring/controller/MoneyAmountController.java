package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.CurrentMonthMoneyAmountDTO;
import com.example.budgetkeeperspring.entity.MoneyAmount;
import com.example.budgetkeeperspring.service.MoneyAmountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class MoneyAmountController {

    public static final String MONEY_AMOUNT_PATH = "/moneyAmount";

    private final MoneyAmountService moneyAmountService;

    @GetMapping(MONEY_AMOUNT_PATH)
    CurrentMonthMoneyAmountDTO getCurrentMonth() {
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return moneyAmountService.getForPeriod(startDate, endDate);
    }

    @PostMapping(MONEY_AMOUNT_PATH)
    ResponseEntity<MoneyAmount> addMoneyAmountForCurrentMonth(@RequestBody Map<String, String> newAmount) {
        MoneyAmount savedMoneyAmount = moneyAmountService.addMoneyAmountForCurrentMonth(newAmount);
        return ResponseEntity.created(URI.create(MONEY_AMOUNT_PATH + savedMoneyAmount.getId())).build();

    }
}
