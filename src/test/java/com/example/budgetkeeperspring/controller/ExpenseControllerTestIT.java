package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ExpenseControllerTestIT {

    @Autowired
    ExpenseController expenseController;

    @Autowired
    ExpenseRepository expenseRepository;

    @Test
    void expense_notFound() {
        assertThrows(NotFoundException.class, () -> {
            expenseController.getById(new Random().nextLong());
        });
    }

    @Test
    void currentMonth_test() {
        expenseRepository.save(Expense.builder()
                .amount(BigDecimal.TEN)
                .title("test_title")
                .transactionDate(LocalDate.now())
                .deleted(false)
                .build());

        List<ExpenseDTO> dtos = expenseController.getCurrentMonth();
        assertThat(dtos.size()).isEqualTo(1);
    }

    @Rollback
    @Transactional
    @Test
    void currentMonth_emptytest() {
        expenseRepository.deleteAll();

        List<ExpenseDTO> dtos = expenseController.getCurrentMonth();
        assertThat(dtos.size()).isEqualTo(0);
    }

    @Test
    void testGetById() {
        expenseRepository.save(Expense.builder()
                .amount(BigDecimal.TEN)
                .title("test_title")
                .transactionDate(LocalDate.now())
                .deleted(false)
                .build());

        Expense expense = expenseRepository.findAll().get(0);

        ExpenseDTO dto = expenseController.getById(expense.getId());

        assertThat(dto).isNotNull();
    }
}