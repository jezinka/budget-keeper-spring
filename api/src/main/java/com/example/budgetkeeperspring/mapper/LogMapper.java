package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.entity.Log;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LogMapper {

    @Mapping(target = "date", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "version", ignore = true)
    Log mapToEntity(LogDTO logDTO);
    @Mapping(target = "date", dateFormat = "yyyy-MM-dd HH:mm:ss")
    LogDTO mapToDto(Log log);
}
