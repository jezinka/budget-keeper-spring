package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.FixedCostDTO;
import com.example.budgetkeeperspring.entity.Circ;
import com.example.budgetkeeperspring.entity.FixedCost;
import com.example.budgetkeeperspring.entity.FixedCostPayed;
import com.example.budgetkeeperspring.repository.FixedCostPayedRepository;
import com.example.budgetkeeperspring.repository.FixedCostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.budgetkeeperspring.utils.DateUtils.getBeginOfCurrentMonth;
import static com.example.budgetkeeperspring.utils.DateUtils.getEndOfCurrentMonth;

@RequiredArgsConstructor
@Service
public class FixedCostService {

    private final FixedCostRepository fixedCostRepository;
    private final FixedCostPayedRepository fixedCostPayedRepository;

    public List<FixedCostDTO> getAllForCurrentMonth() {
        List<FixedCostDTO> fixedCostDTOS = new ArrayList<>();
        List<FixedCost> fixedCosts = fixedCostRepository.findAll();
        List<FixedCostPayed> fixedCostPayed = fixedCostPayedRepository.findAllByPayDateBetween(getBeginOfCurrentMonth(), getEndOfCurrentMonth());
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

    public void updateFixedCost(Long id) {
        Optional<FixedCost> fixedCost = fixedCostRepository.findById(id);
        if (fixedCost.isPresent()) {
            FixedCostPayed fixedCostPayed = FixedCostPayed.builder()
                    .fixedCost(fixedCost.get())
                    .amount(fixedCost.get().getAmount())
                    .payDate(LocalDate.now())
                    .build();
            fixedCostPayedRepository.save(fixedCostPayed);
        }
    }

    public void updateFixedCost(ExpenseDTO expenseDTO) {
        String titleLower = expenseDTO.getTitle().toLowerCase();
        String payeeLower = expenseDTO.getPayee().toLowerCase();

        Optional<FixedCost> fixedCost = findFixedCost(titleLower, payeeLower);

        if (fixedCost.isPresent()) {
            FixedCostPayed fixedCostPayed = FixedCostPayed.builder()
                    .fixedCost(fixedCost.get())
                    .amount(expenseDTO.getAmount())
                    .payDate(LocalDate.parse(expenseDTO.getTransactionDate()))
                    .build();
            fixedCostPayedRepository.save(fixedCostPayed);
        }
    }

    private Optional<FixedCost> findFixedCost(String titleLower, String payeeLower) {
        return fixedCostRepository.findAll()
                .stream()
                .filter(fc -> {
                    Circ c = fc.getCirc();
                    return titleLower.contains(c.getTitle()) && (c.getPayee() == null || payeeLower.contains(c.getPayee()));
                })
                .findFirst();
    }
}
