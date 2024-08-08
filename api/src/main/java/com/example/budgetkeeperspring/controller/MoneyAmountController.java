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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.example.budgetkeeperspring.utils.DateUtils.getBeginOfCurrentMonth;
import static com.example.budgetkeeperspring.utils.DateUtils.getEndOfCurrentMonth;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MoneyAmountController {

    public static final String MONEY_AMOUNT_PATH = "/moneyAmount";

    private final MoneyAmountService moneyAmountService;

    @GetMapping(MONEY_AMOUNT_PATH)
    CurrentMonthMoneyAmountDTO getCurrentMonth() {
        LocalDate startDate = getBeginOfCurrentMonth();
        LocalDate endDate = getEndOfCurrentMonth();
        return moneyAmountService.getForPeriod(startDate, endDate);
    }

    //get for period from request params
    @GetMapping(MONEY_AMOUNT_PATH + "/period")
    CurrentMonthMoneyAmountDTO getForPeriod(@RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        return moneyAmountService.getForPeriod(startDate, endDate);
    }

    @PostMapping(MONEY_AMOUNT_PATH)
    ResponseEntity<MoneyAmountDTO> addMoneyAmountForCurrentMonth(@Validated @RequestBody MoneyAmountDTO newAmount) {
        MoneyAmountDTO savedMoneyAmount = moneyAmountService.addMoneyAmountForCurrentMonth(newAmount);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", MONEY_AMOUNT_PATH + "/" + savedMoneyAmount.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    }
}
