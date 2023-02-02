package com.example.budgetkeeperspring.liability;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:integration-test.properties")
class LiabilityRepositoryTest {

//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    private LiabilityRepository repository;
//
//    @Before
//    public void prepare() {
//        jdbcTemplate.execute("delete from liability_lookout;");
//        jdbcTemplate.execute("delete from liability; ");
//        jdbcTemplate.execute("delete from bank;");
//    }
//
//    @Test
//    void getAll_test() {
//        // given:
//        prepare();
//        jdbcTemplate.execute("INSERT INTO bank (id, name) VALUES (1, 'A');");
//        jdbcTemplate.execute("INSERT INTO liability (id, name, bank_id) VALUES (1, 'Poduszka', 1);");
//        jdbcTemplate.execute("INSERT INTO liability (id, name, bank_id) VALUES (2, 'Kryzysowy', 1);");
//        jdbcTemplate.execute("INSERT INTO liability_lookout (id, date, outcome, liability_id) VALUES (17, '2022-09-30', 210, 1);");
//        jdbcTemplate.execute("INSERT INTO liability_lookout (id, date, outcome, liability_id) VALUES (20, '2022-07-30', 20, 2);");
//        jdbcTemplate.execute("INSERT INTO liability_lookout (id, date, outcome, liability_id) VALUES (9, '2021-07-30', 40, 2);");
//        jdbcTemplate.execute("INSERT INTO liability_lookout (id, date, outcome, liability_id) VALUES (21, '2020-07-30', 10, 2);");
//
//        // when:
//        List<Liability> liabilityList = repository.getAll();
//
//        // then:
//        assertEquals(2, liabilityList.size());
//
//        Liability liability = liabilityList.stream().filter(liability1 -> liability1.getId() == 1).findFirst().get();
//        assertEquals(java.sql.Date.valueOf("2022-09-30"), liability.getDate());
//        assertEquals(210, liability.getOutcome());
//
//        Liability liability2 = liabilityList.stream().filter(liability1 -> liability1.getId() == 2).findFirst().get();
//        assertEquals(java.sql.Date.valueOf("2022-07-30"), liability2.getDate());
//        assertEquals(20, liability2.getOutcome());
//    }
}