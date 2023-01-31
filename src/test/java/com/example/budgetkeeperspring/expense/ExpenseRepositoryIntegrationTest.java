package com.example.budgetkeeperspring.expense;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:integration-test.properties")
public class ExpenseRepositoryIntegrationTest {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//    @Autowired
//    private ExpenseRepository repository;
//
//    @After
//    public void prepare() {
//        jdbcTemplate.execute("delete from transaction");
//    }
//
//    @Test
//    public void findAllForCurrentMonth_testForEmpty() {
//        prepare();
//        List<CurrentMonthExpenses> expenses = repository.findAllForCurrentMonth();
//        assertEquals(0, expenses.size());
//    }
//
//    @Test
//    @Sql(statements = {"INSERT INTO transaction (id, transaction_date, title, payee, amount) VALUES (3821, now(), 'BLIK', '', -43.28);"})
//    public void findAllForCurrentMonth_testForOneRecord() {
//        List<CurrentMonthExpenses> expenses = repository.findAllForCurrentMonth();
//        assertEquals(1, expenses.size());
//    }
//
//    @Test
//    @Sql(statements = {"INSERT INTO transaction (id, transaction_date, title, payee, amount) VALUES (21, '2021-01-01', 'BLIK', '', -43.28);"})
//    public void findAllForCurrentMonth_testForPast() {
//        List<CurrentMonthExpenses> expenses = repository.findAllForCurrentMonth();
//        assertEquals(0, expenses.size());
//    }
//
//    @Test
//    @Sql(statements = {"INSERT INTO transaction (id, transaction_date, title, payee, amount, is_deleted) VALUES (21, '2021-01-01', 'BLIK', '', -43.28, false);"})
//    public void delete_existing() {
//
////      given:
//        long transactionId = 21L;
//
////      when:
//        repository.deleteById(transactionId);
//        Boolean isDeleted = jdbcTemplate.queryForObject("select is_deleted from transaction where id = ?", Boolean.class, transactionId);
//
////      then:
//        assertEquals(true, result);
//        assertEquals(true, isDeleted);
//    }
//
//    @Test
//    public void delete_non_existing() {
//
////      given:
//        long transactionId = 22L;
//
////      when:
//        Boolean result = repository.deleteTransaction(transactionId);
//
////      then:
//        assertEquals(false, result);
//    }
}