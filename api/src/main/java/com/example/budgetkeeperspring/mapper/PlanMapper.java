package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.PlanDTO;
import com.example.budgetkeeperspring.dto.PlannedExpenseDTO;
import com.example.budgetkeeperspring.entity.Plan;
import com.example.budgetkeeperspring.entity.PlannedExpense;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    @Mapping(source = "startDate.year", target = "year")
    @Mapping(source = "startDate.monthValue", target = "month")
    PlanDTO mapToDto(Plan plan);

    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "recurringPayment.id", target = "recurringPaymentId")
    PlannedExpenseDTO mapToDto(PlannedExpense plannedExpense);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plannedExpenses", ignore = true)
    @Mapping(target = "startDate", expression = "java(java.time.YearMonth.of(dto.getYear(), dto.getMonth()).atDay(1))")
    @Mapping(target = "endDate", expression = "java(java.time.YearMonth.of(dto.getYear(), dto.getMonth()).atEndOfMonth())")
    void updatePlanFromDto(PlanDTO dto, @MappingTarget Plan plan);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    @Mapping(target = "recurringPayment", ignore = true)
    @Mapping(source = "dto.amount", target = "amount")
    @Mapping(source = "dto.name", target = "name")
    @Mapping(source = "dto.paid", target = "paid")
    @Mapping(source = "dto.dueDate", target = "dueDate")
    @Mapping(source = "plan", target = "plan")
    void updatePlannedExpenseFromDto(PlannedExpenseDTO dto, Plan plan, @MappingTarget PlannedExpense plannedExpense);
}

