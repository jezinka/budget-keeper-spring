package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.FixedCostDTO;
import com.example.budgetkeeperspring.entity.FixedCost;
import com.example.budgetkeeperspring.mapper.FixedCostMapper;
import com.example.budgetkeeperspring.repository.FixedCostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.budgetkeeperspring.service.DateUtilsService.getBeginOfCurrentMonth;
import static com.example.budgetkeeperspring.service.DateUtilsService.getEndOfCurrentMonth;

@RequiredArgsConstructor
@Service
public class FixedCostService {

    private final FixedCostRepository fixedCostRepository;
    private final FixedCostMapper fixedCostMapper;

    public List<FixedCostDTO> getAllForCurrentMonth() {
        List<FixedCost> fixedCosts = fixedCostRepository.findAllByFixedCostPayedBetween(getBeginOfCurrentMonth(), getEndOfCurrentMonth());
        return fixedCosts.stream().map(fixedCostMapper::mapToDto).toList();
    }
}
