package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.entity.Log;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LogMapper {

    Log mapToEntity(LogDTO logDTO);

    LogDTO mapToDto(Log log);
}
