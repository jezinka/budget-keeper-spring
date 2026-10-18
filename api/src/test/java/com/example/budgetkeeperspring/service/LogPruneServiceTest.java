package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.repository.LogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogPruneServiceTest {

    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private LogPruneService logPruneService;

    @BeforeEach
    void setUp() {
        logPruneService = new LogPruneService(logRepository);
    }

    @Test
    void execute_prunesPlainLogsAfterSevenDaysAndExpenseHistoryAfterAYear() {
        LocalDateTime today = LocalDate.now().atStartOfDay();
        logPruneService.execute();
        verify(logRepository, times(1)).prune(today.minusDays(7), today.minusYears(1));
    }
}
