package com.example.budgetkeeperspring.moneyAmount;

import com.example.budgetkeeperspring.expense.Expense;
import com.example.budgetkeeperspring.expense.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("moneyAmount")
public class MoneyAmountController {

    @Autowired
    MoneyAmountRepository moneyAmountRepository;

    @Autowired
    ExpenseRepository expenseRepository;

    @GetMapping("")
    CurrentMonthMoneyAmount getCurrentMonth() {
        Date startDate = Date.valueOf(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
        Date endDate = Date.valueOf(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()));

        MoneyAmount moneyAmount = moneyAmountRepository.findFirstByDate(startDate);
        if (moneyAmount == null) {
            return new CurrentMonthMoneyAmount();
        }

        AtomicReference<Float> expensesSum = new AtomicReference<>((float) 0);
        AtomicReference<Float> incomeSum = new AtomicReference<>((float) 0);

        expenseRepository.findAllByTransactionDateBetween(startDate, endDate)
                .stream().map(Expense::getAmount).forEach(
                        amount -> {
                            if (amount > 0) {
                                incomeSum.updateAndGet(v -> v + amount);
                            } else {
                                expensesSum.updateAndGet(v -> v + amount);
                            }
                        });

        return new CurrentMonthMoneyAmount(moneyAmount.getAmount(), incomeSum.get(), expensesSum.get());
    }

    @PutMapping("")
    Boolean addMoneyAmountForCurrentMonth(@RequestBody HashMap newAmount) {
        Float amount = Float.valueOf(newAmount.get("amount").toString());
        Date begin = Date.valueOf(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
        MoneyAmount moneyAmount = new MoneyAmount(begin, amount);
        moneyAmountRepository.save(moneyAmount);
        return true;
    }
}
