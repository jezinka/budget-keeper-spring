package com.example.budgetkeeperspring.job;

import com.example.budgetkeeperspring.repository.LogRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class LogPruneJob {

    private final LogRepository logRepository;

    public LogPruneJob(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 8 * * SUN")
    public void execute() {
        log.debug("Prune old logs");
        logRepository.deleteAllByDateBefore(LocalDate.now().minusDays(7));
    }
}