package com.example.budgetkeeperspring.groupedExpenses;

import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:integration-test.properties")
class GroupedExpensesControllerTest {

    @Autowired
    GroupedExpensesController controller;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @After
    public void prepare() {
        jdbcTemplate.execute("delete from transaction;");
        jdbcTemplate.execute("delete from category;");
    }

    @Test
    void getForCurrentYear_empty() {
        // given:
        prepare();

        // when:
        List<GroupedExpenses> transactionList = controller.getForCurrentYear();

        // then:
        assertEquals(0, transactionList.size());
    }

    @Test
    void getForCurrentYear_one_transaction() {
        // given:
        prepare();
        int currentMonth = LocalDate.now().getMonthValue();
        jdbcTemplate.execute("INSERT INTO category (id, name) VALUES (1, 'A');");
        jdbcTemplate.execute("INSERT INTO transaction (id, transaction_date, title, payee, amount, category_id) VALUES (3821, now(), 'BLIK', '', -43.28, 1);");

        // when:
        List<GroupedExpenses> transactionList = controller.getForCurrentYear();

        // then:
        assertEquals(3, transactionList.size());
        assertNotNull(transactionList.stream().filter(t -> t.category.equals("A") && t.month == currentMonth && t.amount == -43.28).findFirst());
        assertNotNull(transactionList.stream().filter(t -> t.category.equals("SUMA") && t.amount == -43.28).findFirst());
        assertNotNull(transactionList.stream().filter(t -> t.month == 99 && t.amount == -43.28).findFirst());
    }
}