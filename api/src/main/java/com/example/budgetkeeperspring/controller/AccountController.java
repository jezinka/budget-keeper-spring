package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.AccountDTO;
import com.example.budgetkeeperspring.entity.AccountTransaction;
import com.example.budgetkeeperspring.service.AccountService;
import com.example.budgetkeeperspring.service.AccountTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Accounts where expenses live")
public class AccountController {

    private final AccountService accountService;
    private final AccountTransactionService accountTransactionService;

    @GetMapping("/all")
    List<AccountDTO> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/sinking-funds")
    List<AccountDTO> getSinkingFunds() {
        return accountService.getSinkingFunds();
    }

    @Operation(summary = "Import a Revolut statement (.xlsx); already imported transactions are skipped")
    @PostMapping("/{id}/import/revolut")
    ResponseEntity<?> importRevolut(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        try {
            return ResponseEntity.ok(accountTransactionService.importRevolutStatement(id, file));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Latest statement balance per account, currency and product, checked against imported transactions")
    @GetMapping("/balances")
    List<AccountTransactionService.StatementBalance> getBalances() {
        return accountTransactionService.getBalances();
    }

    @GetMapping("/{id}/transactions")
    List<AccountTransaction> getTransactions(@PathVariable Long id) {
        return accountTransactionService.getTransactions(id);
    }

    @GetMapping("/{id}/cashflow")
    List<AccountTransactionService.CashflowMonth> getCashflow(@PathVariable Long id) {
        return accountTransactionService.getCashflow(id);
    }
}
