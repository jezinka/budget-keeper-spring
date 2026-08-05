package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.BeneficiaryDTO;
import com.example.budgetkeeperspring.entity.Beneficiary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BeneficiaryMapper {
    Beneficiary mapToEntity(BeneficiaryDTO beneficiaryDTO);
    BeneficiaryDTO mapToDto(Beneficiary beneficiary);
}
