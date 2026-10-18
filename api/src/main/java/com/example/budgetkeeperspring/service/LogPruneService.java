package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.repository.LogRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        // expense history is worth keeping longer than pipeline noise; ~3-5 short rows per expense
        LocalDateTime today = LocalDate.now().atStartOfDay();
        logRepository.prune(today.minusDays(7), today.minusYears(1));
    }
}