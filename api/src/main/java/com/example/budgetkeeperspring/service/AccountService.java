package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.AccountDTO;
import com.example.budgetkeeperspring.mapper.AccountMapper;
import com.example.budgetkeeperspring.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(accountMapper::mapToDto)
                .toList();
    }
}
