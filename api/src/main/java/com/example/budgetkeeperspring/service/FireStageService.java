package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.FireStageDTO;
import com.example.budgetkeeperspring.entity.FireStage;
import com.example.budgetkeeperspring.repository.FireStageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FireStageService {

    private final FireStageRepository fireStageRepository;

    public List<FireStageDTO> findAll() {
        return fireStageRepository.findAllByOrderByThresholdAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public void checkAndMarkCrossedStages(LocalDate date, BigDecimal value) {
        List<FireStage> newlyCrossed = fireStageRepository
                .findByFirstCrossedAtIsNullAndThresholdLessThanEqual(value);

        if (newlyCrossed.isEmpty()) return;

        for (FireStage stage : newlyCrossed) {
            stage.setFirstCrossedAt(date);
            log.info("FIRE stage {} crossed on {}", stage.getThreshold(), date);
        }
        fireStageRepository.saveAll(newlyCrossed);
    }

    private FireStageDTO toDto(FireStage e) {
        return FireStageDTO.builder()
                .id(e.getId())
                .threshold(e.getThreshold())
                .firstCrossedAt(e.getFirstCrossedAt())
                .build();
    }
}
