package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.SavingsReadDTO;
import com.example.budgetkeeperspring.dto.SavingsReadGroupedDTO;
import com.example.budgetkeeperspring.entity.SavingsRead;
import com.example.budgetkeeperspring.mapper.SavingsReadMapper;
import com.example.budgetkeeperspring.repository.SavingsReadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class SavingsReadService {

    private final SavingsReadMapper savingsMapper;
    private final SavingsReadRepository savingsReadRepository;

    public List<SavingsReadDTO> getLatest() {
        return savingsReadRepository.findAll()
                .stream()
                .map(savingsMapper::map)
                // find the latest savingsRead of each saving
                .collect(Collectors.toMap(
                        SavingsReadDTO::getSavingsId,
                        Function.identity(),
                        (a, b) -> a.getDate().isAfter(b.getDate()) ? a : b))
                .values().stream().toList();
    }

    public List<SavingsReadGroupedDTO> getAllGrouped() {
        List<SavingsReadGroupedDTO> grouped = new ArrayList<>();

        for (SavingsRead savingsRead : savingsReadRepository.findAll()) {
            SavingsReadDTO savingsReadDTO = savingsMapper.map(savingsRead);
            SavingsReadGroupedDTO groupedDTO;
            if (grouped.stream().anyMatch(group -> group.getName().equals(savingsReadDTO.getGroupName()))) {
                groupedDTO = grouped.stream().filter(group -> group.getName().equals(savingsReadDTO.getGroupName())).findFirst().get();
                groupedDTO.getData().add(new SavingsReadGroupedDTO.SavingsChartDataDTO(savingsReadDTO));
            } else {
                groupedDTO = new SavingsReadGroupedDTO(savingsReadDTO);
                grouped.add(groupedDTO);
            }
        }
        return grouped;
    }
}
