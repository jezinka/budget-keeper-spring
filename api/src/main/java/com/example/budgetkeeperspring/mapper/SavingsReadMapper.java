package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.SavingsReadDTO;
import com.example.budgetkeeperspring.entity.SavingsRead;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SavingsReadMapper {

    @Mapping(source = "savings.id", target = "savingsId")
    @Mapping(source = "savings.name", target = "name")
    @Mapping(source = "savings.fundGroup", target = "fundGroup")
    @Mapping(source = "savings.purpose", target = "purpose")
    @Mapping(source = "savings.risk", target = "risk")
    @Mapping(source = "savings.autoType", target = "autoType")
    @Mapping(expression = "java(savingsRead.getSavings().getFundGroup() + ' ' + savingsRead.getSavings().getName())", target = "groupName")
    SavingsReadDTO map(SavingsRead savingsRead);

    SavingsRead map(SavingsReadDTO savingsReadDTO);
}
