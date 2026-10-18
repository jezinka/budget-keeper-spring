package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class LogRepositoryTest {

    @Autowired
    LogRepository logRepository;

    @Test
    void prune() {
//given:
        LocalDateTime now = LocalDateTime.now();
        logRepository.save(Log.builder().date(now.minusDays(30)).message("plain old - go").build());
        logRepository.save(Log.builder().date(now.minusDays(2)).message("plain new - stay").build());
        logRepository.save(Log.builder().date(now.minusDays(30)).expenseId(5L).message("expense history - stay").build());
        logRepository.save(Log.builder().date(now.minusYears(2)).expenseId(5L).message("expense ancient - go").build());

//when:
        logRepository.prune(now.minusDays(7), now.minusYears(1));

//then:
        assertEquals(List.of("plain new - stay", "expense history - stay"),
                logRepository.findAll().stream().map(Log::getMessage).sorted(Comparator.reverseOrder()).toList());
    }
}
