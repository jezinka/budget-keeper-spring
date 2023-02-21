package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.CurrentMonthMoneyAmountDTO;
import com.example.budgetkeeperspring.service.MoneyAmountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static com.example.budgetkeeperspring.controller.MoneyAmountController.MONEY_AMOUNT_PATH;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MoneyAmountController.class)
class MoneyAmountControllerTest {

    @MockBean
    MoneyAmountService moneyAmountService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void getCurrentMonth() throws Exception {
        CurrentMonthMoneyAmountDTO dto = new CurrentMonthMoneyAmountDTO(BigDecimal.valueOf(2000), BigDecimal.valueOf(1000), BigDecimal.valueOf(-1500));

        given(moneyAmountService.getForPeriod(any(), any())).willReturn(dto);

        mockMvc.perform(get(MONEY_AMOUNT_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.start", is(2000)))
                .andExpect(jsonPath("$.incomes", is(1000)))
                .andExpect(jsonPath("$.expenses", is(-1500)))
                .andExpect(jsonPath("$.accountBalance", is(1500)));
    }
}