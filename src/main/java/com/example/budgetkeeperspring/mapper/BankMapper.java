package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.BankDTO;
import com.example.budgetkeeperspring.entity.Bank;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BankMapper {

    Bank mapToEntity(BankDTO bank);

    BankDTO mapToDTO(Bank bank);
}
