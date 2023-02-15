package com.example.budgetkeeperspring.category;

import com.example.budgetkeeperspring.expense.Expense;
import com.example.budgetkeeperspring.expense.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository repository;

    @Autowired
    ExpenseRepository expenseRepository;

    @Test
    public void findActiveCategoryShouldReturnEmptyListForNoExpenses() {
        assert repository.findActiveForYear(2023).size() == 0;
    }

    @Test
    @Transactional
    public void findActiveCategoryShouldReturnOneCategory() {
        Category a = repository.save(new Category("testA"));
        Category b = repository.save(new Category("testB"));
        Expense expenseA = new Expense();
        expenseA.setCategory(a);
        expenseA.setAmount(BigDecimal.valueOf(-1.99));
        expenseA.setTransactionDate(LocalDate.of(2020, 12, 1));
        expenseRepository.save(expenseA);

        Expense expenseB = new Expense();
        expenseB.setCategory(b);
        expenseB.setAmount(BigDecimal.valueOf(-1.99));
        expenseB.setTransactionDate(LocalDate.of(2019, 12, 1));
        expenseRepository.save(expenseB);

        List<Category> activeForYear = repository.findActiveForYear(2019);

        assert activeForYear.size() == 1;
        assert activeForYear.stream().allMatch(c -> c.equals(b));
    }
}