package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.CurrentMonthMoneyAmountDTO;
import com.example.budgetkeeperspring.dto.MoneyAmountDTO;
import com.example.budgetkeeperspring.service.MoneyAmountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Slf4j
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
    ResponseEntity<MoneyAmountDTO> addMoneyAmountForCurrentMonth(@Validated @RequestBody MoneyAmountDTO newAmount) {
        MoneyAmountDTO savedMoneyAmount = moneyAmountService.addMoneyAmountForCurrentMonth(newAmount);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", MONEY_AMOUNT_PATH + "/" + savedMoneyAmount.getId().toString());
        return new ResponseEntity(headers, HttpStatus.CREATED);

    }
}
