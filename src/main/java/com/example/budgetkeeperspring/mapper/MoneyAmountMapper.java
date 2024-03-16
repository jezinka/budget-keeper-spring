package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.MoneyAmountDTO;
import com.example.budgetkeeperspring.entity.MoneyAmount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MoneyAmountMapper {

    MoneyAmount mapToEntity(MoneyAmountDTO moneyAmountDTO);

    MoneyAmountDTO mapToDto(MoneyAmount moneyAmount);
}
