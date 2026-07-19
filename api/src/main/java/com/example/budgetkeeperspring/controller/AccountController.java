package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.AccountDTO;
import com.example.budgetkeeperspring.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Accounts where expenses live")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/all")
    List<AccountDTO> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/sinking-funds")
    List<AccountDTO> getSinkingFunds() {
        return accountService.getSinkingFunds();
    }
}
