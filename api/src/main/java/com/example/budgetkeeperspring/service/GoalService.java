package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.GoalDTO;
import com.example.budgetkeeperspring.mapper.GoalMapper;
import com.example.budgetkeeperspring.repository.GoalRepository;
import com.example.budgetkeeperspring.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    public List<GoalDTO> findAllForYear(Integer year) {
        LocalDate begin = DateUtils.getBeginOfSelectedYear(year);
        LocalDate end = DateUtils.getEndOfSelectedYear(year);
        return goalRepository.findAllByDateBetween(begin, end).stream()
                .map(goalMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
