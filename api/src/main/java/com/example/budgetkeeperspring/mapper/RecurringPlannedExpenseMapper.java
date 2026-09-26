 package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.RecurringPlannedExpenseDTO;
import com.example.budgetkeeperspring.entity.Plan;
import com.example.budgetkeeperspring.entity.PlannedExpense;
import com.example.budgetkeeperspring.entity.RecurringPlannedExpense;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RecurringPlannedExpenseMapper {

    RecurringPlannedExpenseDTO mapToDto(RecurringPlannedExpense recurringPayment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateFromDto(RecurringPlannedExpenseDTO dto, @MappingTarget RecurringPlannedExpense recurringPayment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    @Mapping(source = "plan", target = "plan")
    @Mapping(source = "recurringPayment", target = "recurringPayment")
    @Mapping(source = "recurringPayment.name", target = "name")
    @Mapping(source = "recurringPayment.amount", target = "amount")
    @Mapping(target = "paid", ignore = true)
    @Mapping(target = "dueDate", expression = "java(plan.getStartDate().withDayOfMonth(Math.min(recurringPayment.getDueDay(), plan.getEndDate().getDayOfMonth())))")
    PlannedExpense mapToPlannedExpense(Plan plan, RecurringPlannedExpense recurringPayment);
}

