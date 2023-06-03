package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.FixedCostDTO;
import com.example.budgetkeeperspring.entity.FixedCost;
import com.example.budgetkeeperspring.entity.FixedCostPayed;
import com.example.budgetkeeperspring.repository.FixedCostPayedRepository;
import com.example.budgetkeeperspring.repository.FixedCostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.budgetkeeperspring.service.DateUtilsService.getBeginOfCurrentMonth;
import static com.example.budgetkeeperspring.service.DateUtilsService.getEndOfCurrentMonth;

@RequiredArgsConstructor
@Service
public class FixedCostService {

    private final FixedCostRepository fixedCostRepository;
    private final FixedCostPayedRepository fixedCostPayedRepository;

    public List<FixedCostDTO> getAllForCurrentMonth() {
        List<FixedCostDTO> fixedCostDTOS = new ArrayList();
        List<FixedCost> fixedCosts = fixedCostRepository.findAll();
        List<FixedCostPayed> fixedCostPayed = fixedCostPayedRepository.findAllByFixedCostPayedBetween(getBeginOfCurrentMonth(), getEndOfCurrentMonth());
        fixedCosts.forEach(fc -> {
            Optional<FixedCostPayed> fcp = fixedCostPayed.stream().filter(payed -> payed.getFixedCost() == fc).findFirst();
            FixedCostDTO fixedCostDTO = FixedCostDTO.builder()
                    .id(fc.getId())
                    .name(fc.getName())
                    .amount(fcp.isPresent() ? fcp.get().getAmount() : fc.getAmount())
                    .payDate(fcp.map(FixedCostPayed::getPayDate).orElse(null))
                    .build();
            fixedCostDTOS.add(fixedCostDTO);
        });
        return fixedCostDTOS;
    }
}
