package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.entity.LiabilityLookout;
import com.example.budgetkeeperspring.repository.LiabilityLookoutRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LiabilityLookoutService {

    private final LiabilityLookoutRepository liabilityLookoutRepository;

    public LiabilityLookoutService(LiabilityLookoutRepository liabilityLookoutRepository) {
        this.liabilityLookoutRepository = liabilityLookoutRepository;
    }

    public List<LiabilityLookout> getLatest() {
        return liabilityLookoutRepository.retrieveAll().stream()
                .collect(Collectors.toMap(LiabilityLookout::getLiability,
                        Function.identity(),
                        (LiabilityLookout d1, LiabilityLookout d2) -> d1.getDate().compareTo(d2.getDate()) > 0 ? d1 : d2))
                .values().stream().toList();
    }
}