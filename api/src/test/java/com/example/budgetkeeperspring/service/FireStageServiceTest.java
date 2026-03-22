package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.FireStageDTO;
import com.example.budgetkeeperspring.entity.FireStage;
import com.example.budgetkeeperspring.repository.FireStageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FireStageServiceTest {

    @Mock
    FireStageRepository fireStageRepository;

    @InjectMocks
    FireStageService fireStageService;

    @Test
    void findAll_returnsSortedDTOs() {
        FireStage s1 = FireStage.builder().id(1L).threshold(new BigDecimal("75000")).build();
        FireStage s2 = FireStage.builder().id(2L).threshold(new BigDecimal("15000"))
                .firstCrossedAt(LocalDate.of(2021, 4, 1)).build();

        when(fireStageRepository.findAllByOrderByThresholdAsc()).thenReturn(List.of(s2, s1));

        List<FireStageDTO> result = fireStageService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getThreshold()).isEqualByComparingTo("15000");
        assertThat(result.get(0).getFirstCrossedAt()).isEqualTo(LocalDate.of(2021, 4, 1));
        assertThat(result.get(1).getFirstCrossedAt()).isNull();
    }

    @Test
    void checkAndMarkCrossedStages_marksCrossedStages() {
        LocalDate crossDate = LocalDate.of(2024, 6, 1);
        BigDecimal value = new BigDecimal("160000");

        FireStage stage1 = FireStage.builder().id(1L).threshold(new BigDecimal("75000")).build();
        FireStage stage2 = FireStage.builder().id(2L).threshold(new BigDecimal("150000")).build();

        when(fireStageRepository.findByFirstCrossedAtIsNullAndThresholdLessThanEqual(value))
                .thenReturn(List.of(stage1, stage2));

        fireStageService.checkAndMarkCrossedStages(crossDate, value);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<FireStage>> captor = ArgumentCaptor.forClass(List.class);
        verify(fireStageRepository).saveAll(captor.capture());

        List<FireStage> saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved).allMatch(s -> crossDate.equals(s.getFirstCrossedAt()));
    }

    @Test
    void checkAndMarkCrossedStages_doesNothingWhenNoNewStages() {
        BigDecimal value = new BigDecimal("10000");

        when(fireStageRepository.findByFirstCrossedAtIsNullAndThresholdLessThanEqual(value))
                .thenReturn(List.of());

        fireStageService.checkAndMarkCrossedStages(LocalDate.now(), value);

        verify(fireStageRepository, never()).saveAll(any());
    }
}
