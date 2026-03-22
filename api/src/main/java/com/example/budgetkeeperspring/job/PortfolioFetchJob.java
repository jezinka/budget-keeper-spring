package com.example.budgetkeeperspring.job;

import com.example.budgetkeeperspring.service.PortfolioSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PortfolioFetchJob {

    private final PortfolioSnapshotService portfolioSnapshotService;

    @Scheduled(cron = "0 0 18 * * MON-FRI")
    public void execute() {
        log.info("Fetching portfolio snapshot from myFund");
        portfolioSnapshotService.fetchFromMyFundAndSave();
    }
}
