package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class LogRepositoryTest {

    @Autowired
    LogRepository logRepository;

    @Test
    void deleteAllByDateBefore() {
//given:
        logRepository.save(Log.builder().id(1L).date(LocalDateTime.now().minusDays(30)).message("should go").build());
        logRepository.save(Log.builder().id(2L).date(LocalDateTime.now().minusDays(2)).message("should stay").build());

//when:
        logRepository.deleteAllByDateBefore(LocalDateTime.now().minusDays(7));

//then:
        List<Log> allRemainingLogs = logRepository.findAll();
        assertEquals(1, allRemainingLogs.size());
        assertEquals(2, allRemainingLogs.get(0).getId().longValue());
        assertEquals("should stay", allRemainingLogs.get(0).getMessage());
        assertTrue(allRemainingLogs.stream().noneMatch(l -> l.getMessage().equals("should go")));
    }
}