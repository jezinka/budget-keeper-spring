package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.LiabilityDTO;
import com.example.budgetkeeperspring.entity.Liability;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LiabilityMapper {

    Liability mapToEntity(LiabilityDTO liabilityDTO);

    LiabilityDTO mapToDto(Liability liability);
}
