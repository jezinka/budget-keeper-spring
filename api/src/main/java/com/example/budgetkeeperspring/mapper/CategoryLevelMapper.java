package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.CategoryLevelDTO;
import com.example.budgetkeeperspring.entity.CategoryLevel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryLevelMapper {

    CategoryLevel mapToEntity(CategoryLevelDTO categoryLevelDTO);

    CategoryLevelDTO mapToDto(CategoryLevel categoryLevel);
}
