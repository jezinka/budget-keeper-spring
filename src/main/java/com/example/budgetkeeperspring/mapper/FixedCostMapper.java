package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.FixedCostDTO;
import com.example.budgetkeeperspring.entity.FixedCost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper
public interface FixedCostMapper {

    @Mapping(source = "fixedCost.fixedCostPayed.payDate", target = "payDate")
    @Mapping(target = "amount", expression = "java(getAmount(fixedCost))")
    FixedCostDTO mapToDto(FixedCost fixedCost);

    default BigDecimal getAmount(FixedCost fixedCost) {
        if (fixedCost.getFixedCostPayed() != null) {
            return fixedCost.getFixedCostPayed().getAmount();
        }
        return fixedCost.getAmount();
    }
}
