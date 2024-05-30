package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.repository.LogRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class LogPruneService {

    private final LogRepository logRepository;

    public LogPruneService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Transactional
    public void execute() {
        log.info("Prune old logs");
        logRepository.deleteAllByDateBefore(LocalDate.now().atStartOfDay().minusDays(7));
    }
}