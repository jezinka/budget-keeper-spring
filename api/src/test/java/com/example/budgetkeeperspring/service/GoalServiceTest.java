package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.entity.Goal;
import com.example.budgetkeeperspring.mapper.GoalMapper;
import com.example.budgetkeeperspring.repository.GoalRepository;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GoalServiceTest {

    private final GoalRepository goalRepository = mock(GoalRepository.class);
    private final GoalService service = new GoalService(goalRepository, Mappers.getMapper(GoalMapper.class));

    @Test
    void findsGoalsWithinWholeSelectedYear() {
        Goal goal = Goal.builder().id(1L).amount(new BigDecimal("1000")).date(LocalDate.of(2026, 6, 1)).build();
        when(goalRepository.findAllByDateBetween(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31))).thenReturn(List.of(goal));

        assertEquals(1, service.findAllForYear(2026).size());
    }

    @Test
    void filtersGoalsByCategoryLevelWithinYear() {
        Goal goal = Goal.builder().id(1L).amount(new BigDecimal("1000")).date(LocalDate.of(2026, 6, 1)).build();
        when(goalRepository.findAllByDateBetweenAndCategory_LevelIsIn(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), List.of(2)))
                .thenReturn(List.of(goal));

        assertEquals(1, service.findAllForYearAndCategoryLevel(2026, List.of(2)).size());
    }
}
