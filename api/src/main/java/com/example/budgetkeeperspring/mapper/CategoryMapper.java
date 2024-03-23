package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.CategoryDTO;
import com.example.budgetkeeperspring.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "toCheck", ignore = true)
    Category mapToEntity(CategoryDTO categoryDTO);

    CategoryDTO mapToDto(Category category);
}
