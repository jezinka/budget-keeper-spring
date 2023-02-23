package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.mapper.ExpenseMapper;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(dtos).hasSize(1);
    }

    @Rollback
    @Transactional
    @Test
    void currentMonth_emptytest() {
        expenseRepository.deleteAll();

        List<ExpenseDTO> dtos = expenseController.getCurrentMonth();
        assertThat(dtos.size()).isZero();
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

    @Test
    void edit_NotFound() {
        assertThrows(NotFoundException.class, () -> {
            expenseController.editExpense(new Random().nextLong(), ExpenseDTO.builder().build());
        });
    }
}