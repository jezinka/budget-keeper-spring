package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.FixedCostDTO;
import com.example.budgetkeeperspring.entity.FixedCost;
import com.example.budgetkeeperspring.entity.FixedCostPayed;
import com.example.budgetkeeperspring.repository.FixedCostPayedRepository;
import com.example.budgetkeeperspring.repository.FixedCostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FixedCostServiceTest {

    @Mock
    FixedCostRepository fixedCostRepository;

    @Mock
    FixedCostPayedRepository fixedCostPayedRepository;

    @Autowired
    @InjectMocks
    FixedCostService fixedCostService;

    @Test
    void getAllForCurrentMonth() {

        FixedCost a = new FixedCost();
        a.setId(1L);
        a.setAmount(BigDecimal.TEN);

        FixedCost b = new FixedCost();
        b.setId(2L);
        b.setAmount(BigDecimal.ONE);

        FixedCostPayed c = new FixedCostPayed();
        c.setFixedCost(a);
        c.setPayDate(LocalDate.now());
        c.setAmount(new BigDecimal("10.23"));

        when(fixedCostRepository
                .findAll())
                .thenReturn(Arrays.asList(a, b));

        when(fixedCostPayedRepository.findAllByPayDateBetween(any(), any())).thenReturn(List.of(c));

        List<FixedCostDTO> result = fixedCostService.getAllForCurrentMonth();

        Assertions.assertEquals(result.size(), 2);
        Assertions.assertTrue(result.stream().anyMatch(fc -> fc.getAmount().equals(new BigDecimal("10.23"))));
        Assertions.assertTrue(result.stream().anyMatch(fc -> fc.getAmount().equals(BigDecimal.ONE)));
    }

    @Test
    void getAllForCurrentMonth_EmptyArrays() {

        when(fixedCostRepository
                .findAll())
                .thenReturn(new ArrayList<>());

        when(fixedCostPayedRepository.findAllByPayDateBetween(any(), any())).thenReturn(new ArrayList<>());

        List<FixedCostDTO> result = fixedCostService.getAllForCurrentMonth();

        Assertions.assertEquals(result.size(), 0);
    }
}