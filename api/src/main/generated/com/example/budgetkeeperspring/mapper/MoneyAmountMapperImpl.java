package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.MoneyAmountDTO;
import com.example.budgetkeeperspring.entity.MoneyAmount;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-07T14:04:53+0000",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.4.jar, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class MoneyAmountMapperImpl implements MoneyAmountMapper {

    @Override
    public MoneyAmount mapToEntity(MoneyAmountDTO moneyAmountDTO) {
        if ( moneyAmountDTO == null ) {
            return null;
        }

        MoneyAmount.MoneyAmountBuilder moneyAmount = MoneyAmount.builder();

        moneyAmount.id( moneyAmountDTO.getId() );
        moneyAmount.version( moneyAmountDTO.getVersion() );
        moneyAmount.date( moneyAmountDTO.getDate() );
        moneyAmount.amount( moneyAmountDTO.getAmount() );
        moneyAmount.createdAt( moneyAmountDTO.getCreatedAt() );

        return moneyAmount.build();
    }

    @Override
    public MoneyAmountDTO mapToDto(MoneyAmount moneyAmount) {
        if ( moneyAmount == null ) {
            return null;
        }

        MoneyAmountDTO.MoneyAmountDTOBuilder moneyAmountDTO = MoneyAmountDTO.builder();

        moneyAmountDTO.id( moneyAmount.getId() );
        moneyAmountDTO.version( moneyAmount.getVersion() );
        moneyAmountDTO.date( moneyAmount.getDate() );
        moneyAmountDTO.amount( moneyAmount.getAmount() );
        moneyAmountDTO.createdAt( moneyAmount.getCreatedAt() );

        return moneyAmountDTO.build();
    }
}
