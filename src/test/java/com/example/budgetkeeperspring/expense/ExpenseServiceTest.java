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
class ExpenseServiceTest {

//    @Autowired
//    ExpenseService service;
//
//    @Autowired
//    ExpenseRepository expenseRepository;
//
//    @Autowired
//    JdbcTemplate jdbcTemplate;
//
//    public void prepare() {
//        jdbcTemplate.execute("delete from transaction;");
//        jdbcTemplate.execute("delete from category; ");
//        jdbcTemplate.execute("INSERT INTO transaction (id, transaction_date, title, payee, amount) VALUES (3821, now(), 'BLIK', '', -15.70);");
//        jdbcTemplate.execute("INSERT INTO category (id, name) VALUES (1, 'A');");
//        jdbcTemplate.execute("INSERT INTO category (id, name) VALUES (2, 'B');");
//    }
//
//    @Test()
//    void splitTransaction() {
//        prepare();
//        // given:
//        Long id = 3821L;
//        Expense t1 = expenseRepository.getById(id);
//        t1.setAmount(-10.20f);
//        t1.setCategoryId(1L);
//        Expense t2 = expenseRepository.getById(id);
//        t2.setAmount(null);
//        t1.setCategoryId(2L);
//        List<Expense> expenseList = new ArrayList<>(List.of(t1, t2));
//
//        // when:
//        Boolean result = service.splitExpanse(id, expenseList);
//
//        // then:
//        assertEquals(false, result);
//        Expense t = expenseRepository.getById(id);
//        assertEquals(false, t.getDeleted());
//        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from transaction", Integer.class));
//    }
}