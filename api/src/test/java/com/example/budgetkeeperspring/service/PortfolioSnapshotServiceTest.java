package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.PortfolioSnapshotDTO;
import com.example.budgetkeeperspring.entity.PortfolioSnapshot;
import com.example.budgetkeeperspring.repository.PortfolioSnapshotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioSnapshotServiceTest {

    @Mock
    PortfolioSnapshotRepository portfolioSnapshotRepository;

    @Mock
    FireStageService fireStageService;

    @InjectMocks
    PortfolioSnapshotService portfolioSnapshotService;

    @Test
    void findAll_returnsMappedDTOs() {
        PortfolioSnapshot s1 = PortfolioSnapshot.builder()
                .id(1L).date(LocalDate.of(2024, 1, 1)).value(new BigDecimal("100000")).build();
        PortfolioSnapshot s2 = PortfolioSnapshot.builder()
                .id(2L).date(LocalDate.of(2024, 2, 1)).value(new BigDecimal("120000")).build();

        when(portfolioSnapshotRepository.findAllByOrderByDateAsc()).thenReturn(List.of(s1, s2));

        List<PortfolioSnapshotDTO> result = portfolioSnapshotService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getValue()).isEqualByComparingTo("100000");
        assertThat(result.get(1).getDate()).isEqualTo(LocalDate.of(2024, 2, 1));
    }

    @Test
    void save_persistsSnapshotAndReturnsDTO() {
        LocalDate date = LocalDate.of(2024, 3, 1);
        BigDecimal value = new BigDecimal("150000");

        when(portfolioSnapshotRepository.findByDate(date)).thenReturn(Optional.empty());
        when(portfolioSnapshotRepository.save(any())).thenAnswer(inv -> {
            PortfolioSnapshot s = inv.getArgument(0);
            s = PortfolioSnapshot.builder().id(42L).date(s.getDate()).value(s.getValue()).build();
            return s;
        });

        PortfolioSnapshotDTO result = portfolioSnapshotService.save(date, value);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getValue()).isEqualByComparingTo("150000");

        ArgumentCaptor<PortfolioSnapshot> captor = ArgumentCaptor.forClass(PortfolioSnapshot.class);
        verify(portfolioSnapshotRepository).save(captor.capture());
        assertThat(captor.getValue().getDate()).isEqualTo(date);
    }

    @Test
    void save_skipsDuplicateDate() {
        LocalDate date = LocalDate.of(2024, 3, 1);
        when(portfolioSnapshotRepository.findByDate(date))
                .thenReturn(Optional.of(PortfolioSnapshot.builder().id(1L).date(date).value(BigDecimal.TEN).build()));

        PortfolioSnapshotDTO result = portfolioSnapshotService.save(date, new BigDecimal("999"));

        assertThat(result).isNull();
        verify(portfolioSnapshotRepository, never()).save(any());
    }

    @Test
    void save_callsFireStageCheck() {
        LocalDate date = LocalDate.of(2024, 5, 1);
        BigDecimal value = new BigDecimal("200000");

        when(portfolioSnapshotRepository.findByDate(date)).thenReturn(Optional.empty());
        when(portfolioSnapshotRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        portfolioSnapshotService.save(date, value);

        verify(fireStageService).checkAndMarkCrossedStages(date, value);
    }
}
