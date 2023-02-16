package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(source = "deleted", target = "deleted", defaultValue = "false")
    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "categoryName", target = "category.name")
    Expense mapToEntity(ExpenseDTO expenseDTO);

    @Mapping(source = "deleted", target = "deleted", defaultValue = "false")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ExpenseDTO mapToDTO(Expense expense);

}
