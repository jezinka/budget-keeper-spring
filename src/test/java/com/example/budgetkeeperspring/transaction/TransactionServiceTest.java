package com.example.budgetkeeperspring.transaction;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:integration-test.properties")
class TransactionServiceTest {

    @Autowired
    TransactionService service;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void prepare() {
        jdbcTemplate.execute("delete from transaction;");
        jdbcTemplate.execute("delete from category; ");
        jdbcTemplate.execute("INSERT INTO transaction (id, transaction_date, title, payee, amount) VALUES (3821, now(), 'BLIK', '', -15.70);");
        jdbcTemplate.execute("INSERT INTO category (id, name) VALUES (1, 'A');");
        jdbcTemplate.execute("INSERT INTO category (id, name) VALUES (2, 'B');");
    }

    @Test()
    void splitTransaction() {
        prepare();
        // given:
        Long id = 3821L;
        Transaction t1 = transactionRepository.getById(id);
        t1.setAmount(-10.20f);
        t1.setCategoryId(1L);
        Transaction t2 = transactionRepository.getById(id);
        t2.setAmount(null);
        t1.setCategoryId(2L);
        List<Transaction> transactionList = new ArrayList<>(List.of(t1, t2));

        // when:
        Boolean result = service.splitTransaction(id, transactionList);

        // then:
        assertEquals(false, result);
        Transaction t = transactionRepository.getById(id);
        assertEquals(false, t.getIsDeleted());
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from transaction", Integer.class));
    }
}