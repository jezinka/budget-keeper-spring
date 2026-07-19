package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.AccountDTO;
import com.example.budgetkeeperspring.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDTO mapToDto(Account account);
}
