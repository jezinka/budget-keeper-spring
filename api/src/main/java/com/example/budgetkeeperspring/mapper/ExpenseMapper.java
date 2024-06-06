package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(source = "deleted", target = "deleted", defaultValue = "false")
    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "categoryName", target = "category.name")
    @Mapping(source = "transactionDate", target = "transactionDate")
    Expense mapToEntity(ExpenseDTO expenseDTO);

    @Mapping(source = "deleted", target = "deleted", defaultValue = "false")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ExpenseDTO mapToDto(Expense expense);

    default LocalDate stringToLocalDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatter);
        }
    }
}
