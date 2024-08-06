package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.GoalDTO;
import com.example.budgetkeeperspring.mapper.GoalMapper;
import com.example.budgetkeeperspring.repository.GoalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    public List<GoalDTO> findAllForYear(Integer year) {
        LocalDate begin = LocalDate.of(year, Month.JANUARY, 1);
        LocalDate end = LocalDate.of(year, Month.DECEMBER, 31);
        return goalRepository.findAllByDateBetween(begin, end).stream()
                .map(goalMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
