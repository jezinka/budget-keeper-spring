package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.LiabilityLookoutDTO;
import com.example.budgetkeeperspring.entity.LiabilityLookout;
import com.example.budgetkeeperspring.mapper.LiabilityLookoutMapper;
import com.example.budgetkeeperspring.repository.LiabilityLookoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LiabilityLookoutService {

    private final LiabilityLookoutRepository liabilityLookoutRepository;
    private final LiabilityLookoutMapper liabilityLookoutMapper;

    public List<LiabilityLookoutDTO> getLatest() {
        return liabilityLookoutRepository.retrieveAll().stream()
                .collect(Collectors.toMap(LiabilityLookout::getLiability,
                        Function.identity(),
                        (LiabilityLookout d1, LiabilityLookout d2) -> d1.getDate().compareTo(d2.getDate()) > 0 ? d1 : d2))
                .values().stream().map(liabilityLookoutMapper::mapToDto).toList();
    }
}
