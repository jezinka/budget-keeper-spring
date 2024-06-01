package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.CategoryDTO;
import com.example.budgetkeeperspring.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category mapToEntity(CategoryDTO categoryDTO);

    CategoryDTO mapToDto(Category category);
}
