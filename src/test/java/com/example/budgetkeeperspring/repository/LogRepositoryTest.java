package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Log;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertTrue;

@DataJpaTest
class LogRepositoryTest {

    @Autowired
    LogRepository logRepository;

    @Test
    void deleteAllByDateBefore() {
//given:
        logRepository.save(Log.builder().id(1L).date(LocalDate.now().minusDays(30)).message("should go").build());
        logRepository.save(Log.builder().id(2L).date(LocalDate.now().minusDays(2)).message("should stay").build());

//when:
        logRepository.deleteAllByDateBefore(LocalDate.now().minusDays(7));

//then:
        List<Log> allRemainingLogs = logRepository.findAll();
        Assert.assertEquals(1, allRemainingLogs.size());
        Assert.assertEquals(2, allRemainingLogs.get(0).getId().longValue());
        Assert.assertEquals("should stay", allRemainingLogs.get(0).getMessage());
        assertTrue(allRemainingLogs.stream().noneMatch(l -> l.getMessage().equals("should go")));
    }
}