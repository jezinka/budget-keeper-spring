package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.BeneficiaryDTO;
import com.example.budgetkeeperspring.mapper.BeneficiaryMapper;
import com.example.budgetkeeperspring.repository.BeneficiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

@RequiredArgsConstructor
public class BeneficiaryService {

    private final BeneficiaryMapper mapper;
    private final BeneficiaryRepository beneficiaryRepository;

    public List<BeneficiaryDTO> getAllBeneficiaries() {
        return beneficiaryRepository.findAll()
                .stream()
                .map(mapper::mapToDto)
                .toList();
    }

}
