package com.example.budgetkeeperspring.liability;

import com.example.budgetkeeperspring.bank.Bank;
import com.example.budgetkeeperspring.bank.BankRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.util.List;

import static java.sql.Date.valueOf;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:integration-test.properties")
public class LiabilityLookoutControllerTest {

    @Autowired
    public LiabilityLookoutController liabilityLookoutController;
    @Autowired
    public LiabilityLookoutRepository liabilityLookoutRepository;
    @Autowired
    public LiabilityRepository liabilityRepository;
    @Autowired
    public BankRepository bankRepository;

    @Before
    public void prepare() {
        liabilityLookoutRepository.deleteAll();
        liabilityRepository.deleteAll();
        bankRepository.deleteAll();
    }

    @Test
    public void getLatest_test() {
        // given:
        Bank bank = bankRepository.save(new Bank(1L, "A"));

        Liability l1 = createLiability(bank, "Poduszka");
        Liability l2 = createLiability(bank, "Kryzysowy");

        createLiabilityLookout(l1, valueOf("2022-09-30"), 210f);
        createLiabilityLookout(l1, valueOf("2021-09-30"), 20f);
        createLiabilityLookout(l2, valueOf("2022-07-30"), 20f);
        createLiabilityLookout(l2, valueOf("2021-07-30"), 40f);
        createLiabilityLookout(l2, valueOf("2020-07-30"), 10f);

        // when:
        List<LiabilityLookout> liabilityList = liabilityLookoutController.getLatest();

        // then:
        assertEquals(2, liabilityList.size());
        var ll1 = liabilityList.stream().filter(l -> l.getOutcome() == 210f).findFirst().get();
        var ll2 = liabilityList.stream().filter(l -> l.getOutcome() == 20f).findFirst().get();

        assertNotEquals(ll1.liability, ll2.liability);
        assertNotNull(ll1);
        assertNotNull(ll2);
    }

    private void createLiabilityLookout(Liability l, Date date, Float outcome) {
        LiabilityLookout liabilityLookout = new LiabilityLookout();
        liabilityLookout.setLiability(l);
        liabilityLookout.setDate(date);
        liabilityLookout.setOutcome(outcome);
        liabilityLookoutRepository.save(liabilityLookout);
    }

    private Liability createLiability(Bank bank, String name) {
        Liability l2 = new Liability();
        l2.setBank(bank);
        l2.setName(name);
        return liabilityRepository.save(l2);
    }
}