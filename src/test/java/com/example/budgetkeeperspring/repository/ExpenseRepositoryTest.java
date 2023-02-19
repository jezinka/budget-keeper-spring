package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Expense;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ExpenseRepositoryTest {

    @Autowired
    ExpenseRepository repository;

    @Test
    void retrieveAll_ShouldReturnOnlyUndeletedEntities() {

        Expense expenseA = new Expense();
        expenseA.setAmount(BigDecimal.valueOf(-1.99));
        expenseA.setTransactionDate(LocalDate.of(2020, 12, 1));
        expenseA.setDeleted(true);
        repository.save(expenseA);

        Expense expenseB = new Expense();
        expenseA.setAmount(BigDecimal.valueOf(-1.99));
        expenseA.setTransactionDate(LocalDate.of(2020, 12, 1));
        repository.save(expenseB);

        List<Expense> all = repository.findAllByOrderByTransactionDateDesc();
        assertEquals(1, all.size());
        assertTrue(all.stream().noneMatch(Expense::getDeleted));
    }
}