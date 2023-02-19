package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.LiabilityLookoutDTO;
import com.example.budgetkeeperspring.entity.LiabilityLookout;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LiabilityLookoutMapper {

    @Mapping(source = "liabilityId", target = "liability.id")
    @Mapping(source = "liabilityName", target = "liability.name")
    @Mapping(source = "bankId", target = "liability.bank.id")
    @Mapping(source = "bankName", target = "liability.bank.name")
    LiabilityLookout mapToEntity(LiabilityLookoutDTO liabilityLookoutDTO);

    @Mapping(source = "liability.id", target = "liabilityId")
    @Mapping(source = "liability.name", target = "liabilityName")
    @Mapping(source = "liability.bank.id", target = "bankId")
    @Mapping(source = "liability.bank.name", target = "bankName")
    LiabilityLookoutDTO mapToDto(LiabilityLookout liabilityLookout);
}
