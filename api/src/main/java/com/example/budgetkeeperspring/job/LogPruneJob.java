package com.example.budgetkeeperspring.job;

import com.example.budgetkeeperspring.service.LogPruneService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogPruneJob {

    private final LogPruneService logPruneService;

    public LogPruneJob(LogPruneService logPruneService) {
        this.logPruneService = logPruneService;
    }

    @PostConstruct
    public void init() {
        logPruneService.execute();
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void execute() {
        logPruneService.execute();
    }
}