package com.example.budgetkeeperspring.job;

import com.example.budgetkeeperspring.service.MoneyAmountService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MoneyAmountSetJob {

    private final MoneyAmountService moneyAmountService;

    public MoneyAmountSetJob(MoneyAmountService moneyAmountService) {
        this.moneyAmountService = moneyAmountService;
    }

    @Transactional
    @Scheduled(cron = "0 50 23 L * *")
    public void execute() {
        log.debug("Add money amount for next month");
        moneyAmountService.addMoneyAmountForNextMonth();
    }
}
