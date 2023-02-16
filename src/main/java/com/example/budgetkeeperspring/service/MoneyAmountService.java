package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.CurrentMonthMoneyAmountDTO;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.entity.MoneyAmount;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import com.example.budgetkeeperspring.repository.MoneyAmountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MoneyAmountService {

    private final MoneyAmountRepository moneyAmountRepository;
    private final ExpenseRepository expenseRepository;

    public MoneyAmountService(MoneyAmountRepository moneyAmountRepository, ExpenseRepository expenseRepository) {
        this.moneyAmountRepository = moneyAmountRepository;
        this.expenseRepository = expenseRepository;
    }

    public CurrentMonthMoneyAmountDTO getForPeriod(LocalDate startDate, LocalDate endDate) {

        MoneyAmount moneyAmount = moneyAmountRepository.findFirstByDate(startDate);
        if (moneyAmount == null) {
            return new CurrentMonthMoneyAmountDTO();
        }

        List<BigDecimal> incomes = new ArrayList<>();
        List<BigDecimal> expenses = new ArrayList<>();

        expenseRepository.findAllByTransactionDateBetween(startDate, endDate)
                .stream()
                .map(Expense::getAmount)
                .forEach(amount -> {
                    if (amount.compareTo(BigDecimal.ZERO) < 0) {
                        expenses.add(amount);
                    } else {
                        incomes.add(amount);
                    }
                });

        BigDecimal expensesSum = expenses.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal incomeSum = incomes.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CurrentMonthMoneyAmountDTO(moneyAmount.getAmount(), incomeSum, expensesSum);
    }

    public Boolean addMoneyAmountForCurrentMonth(Map<String, String> newAmount) {
        BigDecimal amount = new BigDecimal(newAmount.get("amount"));
        LocalDate begin = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());

        MoneyAmount moneyAmount = moneyAmountRepository.findFirstByDate(begin);
        if (moneyAmount != null) {
            moneyAmount.setAmount(amount);
        } else {
            moneyAmount = new MoneyAmount(begin, amount);
        }
        moneyAmountRepository.save(moneyAmount);
        return true;
    }
}
