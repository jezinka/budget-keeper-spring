package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.LiabilityLookoutDTO;
import com.example.budgetkeeperspring.entity.Liability;
import com.example.budgetkeeperspring.entity.LiabilityLookout;
import com.example.budgetkeeperspring.mapper.LiabilityLookoutMapper;
import com.example.budgetkeeperspring.repository.LiabilityLookoutRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LiabilityLookoutServiceTest {

    @Mock
    LiabilityLookoutRepository liabilityLookoutRepository;

    @Spy
    private LiabilityLookoutMapper liabilityLookoutMapper = Mappers.getMapper(LiabilityLookoutMapper.class);

    @Autowired
    @InjectMocks
    LiabilityLookoutService liabilityLookoutService;

    @Test
    void getLatest() {

        Liability liabilityA = new Liability();
        liabilityA.setName("liabilityA");

        LiabilityLookout llA = new LiabilityLookout();
        llA.setLiability(liabilityA);
        llA.setOutcome(BigDecimal.valueOf(-12.99));
        llA.setDate(LocalDate.of(2023, 1, 12));

        LiabilityLookout llAa = new LiabilityLookout();
        llAa.setLiability(liabilityA);
        llAa.setOutcome(BigDecimal.valueOf(-112.99));
        llAa.setDate(LocalDate.of(2023, 1, 10));


        when(liabilityLookoutRepository
                .retrieveAll())
                .thenReturn(List.of(llA, llAa));

        List<LiabilityLookoutDTO> result = liabilityLookoutService.getLatest();

        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(ll -> ll.getOutcome().equals(BigDecimal.valueOf(-12.99))));

    }
}