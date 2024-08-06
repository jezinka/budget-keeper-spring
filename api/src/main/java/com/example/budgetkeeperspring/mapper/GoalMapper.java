package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.GoalDTO;
import com.example.budgetkeeperspring.entity.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(source = "categoryName", target = "category.name")
    Goal mapToEntity(GoalDTO goalDTO);

    @Mapping(source = "category.name", target = "categoryName")
    GoalDTO mapToDto(Goal goal);
}
