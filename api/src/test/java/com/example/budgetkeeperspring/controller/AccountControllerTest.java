package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.AccountDTO;
import com.example.budgetkeeperspring.service.AccountService;
import com.example.budgetkeeperspring.service.AccountTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @MockitoBean
    AccountService accountService;

    @MockitoBean
    AccountTransactionService accountTransactionService;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void returnsSinkingFunds() throws Exception {
        AccountDTO fund = new AccountDTO();
        fund.setBalance(new BigDecimal("130.00"));
        given(accountService.getSinkingFunds()).willReturn(List.of(fund));

        mockMvc.perform(get("/accounts/sinking-funds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void invalidRevolutStatementIs400WithMessage() throws Exception {
        given(accountTransactionService.importRevolutStatement(eq(1L), any()))
                .willThrow(new IllegalArgumentException("To nie jest wyciąg Revolut"));

        mockMvc.perform(multipart("/accounts/1/import/revolut").file(new MockMultipartFile("file", "x.xlsx", null, new byte[]{1})))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("To nie jest wyciąg Revolut"));
    }
}
