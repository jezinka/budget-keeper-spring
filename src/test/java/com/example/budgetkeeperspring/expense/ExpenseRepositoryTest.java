package com.example.budgetkeeperspring.expense;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ExpenseRepositoryTest {

    @Autowired
    ExpenseRepository repository;

    @Test
    public void retrieveAll_ShouldReturnOnlyUndeletedEntities() {

        Expense expenseA = new Expense();
        expenseA.setAmount(BigDecimal.valueOf(-1.99));
        expenseA.setTransactionDate(LocalDate.of(2020, 12, 1));
        expenseA.setDeleted(true);
        repository.save(expenseA);

        Expense expenseB = new Expense();
        expenseA.setAmount(BigDecimal.valueOf(-1.99));
        expenseA.setTransactionDate(LocalDate.of(2020, 12, 1));
        repository.save(expenseB);


        List<Expense> all = repository.retrieveAll();
        assert all.size() == 1;
        assert all.stream().noneMatch(Expense::getDeleted);

    }
}