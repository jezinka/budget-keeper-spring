package com.example.budgetkeeperspring.expense;

import com.example.budgetkeeperspring.category.Category;
import com.example.budgetkeeperspring.category.CategoryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:integration-test.properties")
public class ExpenseServiceTest {

    @Autowired
    public ExpenseService service;
    @Autowired
    public ExpenseRepository expenseRepository;

    @Autowired
    public CategoryRepository categoryRepository;


    @Transactional
    public void prepare() {
        expenseRepository.deleteAll();
        categoryRepository.deleteAll();

        Category categoryA = categoryRepository.save(new Category("A"));
        Category categoryB = categoryRepository.save(new Category("B"));

        Expense e = new Expense();
        e.setAmount(-15.70f);
        e.setTransactionDate(Date.valueOf("2023-10-14"));
        e.setCategory(categoryA);
        expenseRepository.save(e);
    }

    @Test()
    public void splitExpense_Test() {
        prepare();
        // given:
        Expense e = expenseRepository.findAll().stream().findFirst().get();
        Expense t1 = new Expense();
        BeanUtils.copyProperties(e, t1);
        t1.setId(null);
        t1.setAmount(-10.20f);
        t1.setDeleted(false);

        Expense t2 = new Expense();
        BeanUtils.copyProperties(e, t2);
        t2.setId(null);
        t2.setAmount(-5.50f);
        t2.setCategory(categoryRepository.findByName("B"));
        t2.setDeleted(false);

        List<Expense> expenseList = new ArrayList<>(List.of(t1, t2));

        // when:
        service.splitExpense(e.getId(), expenseList);
        List<Expense> all = expenseRepository.findAll();

        // then:
        assertEquals(2, all.size());
        assertTrue(all.stream().noneMatch(expense -> expense.getId() == e.getId()));
    }
}