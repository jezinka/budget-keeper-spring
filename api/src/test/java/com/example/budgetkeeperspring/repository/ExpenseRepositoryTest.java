package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Expense;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    ExpenseRepository repository;

    @Test
    void save_tooLongName_test() {

        assertThrows(ConstraintViolationException.class, () -> {
            Expense expense = repository.save(Expense.builder()
                    .amount(BigDecimal.TEN)
                    .title("test tdfjlasjdfkdsjkfjdsklfjkadsjfkdsjfkdsjfksdjkfdsklfkldsjfkdsjfkjakfjdksjfkdsjfklaskldjskfjdkslfjkldsjfkldsjfkdsjkfjdsklfjklajfdsjfasfitletdfjlasjdfkdsjkfjdsklfjkadsjfkdsjfkdsjfksdjkfdsklfkldsjfkdsjfkjakfjdksjfkdsjfklaskldjskfjdkslfjkldsjfkldsjfkdsjkfjdsklfjklajfdsjfasfitle")
                    .transactionDate(LocalDate.now())
                    .build());
            repository.flush();
        });
    }

    @Test
    void save_test() {
        Expense expense = repository.save(Expense.builder()
                .amount(BigDecimal.TEN)
                .title("test title")
                .transactionDate(LocalDate.now())
                .build());

        assertThat(expense.getId()).isNotNull();
    }

    @Test
    void retrieveAll_ShouldReturnOnlyUndeletedEntities() {

        repository.save(Expense.builder()
                .amount(BigDecimal.valueOf(-1.99))
                .transactionDate(LocalDate.of(2020, 12, 1))
                .deleted(true)
                .build());

        repository.save(Expense.builder()
                .amount(BigDecimal.valueOf(-1.99))
                .transactionDate(LocalDate.of(2020, 12, 1))
                .build());

        List<Expense> all = repository.findAllByOrderByTransactionDateDesc();
        assertEquals(1, all.size());
        assertTrue(all.stream().noneMatch(Expense::isDeleted));
    }
}