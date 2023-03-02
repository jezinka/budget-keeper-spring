package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.CurrentMonthMoneyAmountDTO;
import com.example.budgetkeeperspring.dto.MoneyAmountDTO;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.entity.MoneyAmount;
import com.example.budgetkeeperspring.mapper.MoneyAmountMapper;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import com.example.budgetkeeperspring.repository.MoneyAmountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MoneyAmountService {

    private final MoneyAmountRepository moneyAmountRepository;
    private final ExpenseRepository expenseRepository;
    private final MoneyAmountMapper moneyAmountMapper;

    public CurrentMonthMoneyAmountDTO getForPeriod(LocalDate startDate, LocalDate endDate) {

        MoneyAmount moneyAmount;
        Optional<MoneyAmount> repositoryFirstByDate = moneyAmountRepository.findFirstByDateOrderByCreatedAtDesc(startDate);

        if (repositoryFirstByDate.isEmpty()) {
            return new CurrentMonthMoneyAmountDTO();
        } else {
            moneyAmount = repositoryFirstByDate.get();
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

    public MoneyAmountDTO addMoneyAmountForCurrentMonth(MoneyAmountDTO newAmount) {
        return moneyAmountMapper.mapToDto(moneyAmountRepository.save(moneyAmountMapper.mapToEntity(newAmount)));
    }
}
