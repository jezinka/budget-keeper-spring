package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.LiabilityLookoutDTO;
import com.example.budgetkeeperspring.entity.LiabilityLookout;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LiabilityLookoutMapper {

    LiabilityLookout mapToEntity(LiabilityLookoutDTO liabilityLookoutDTO);

    LiabilityLookoutDTO mapToDto(LiabilityLookout liabilityLookout);
}
