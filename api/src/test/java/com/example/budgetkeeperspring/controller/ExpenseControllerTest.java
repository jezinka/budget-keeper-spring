package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @MockBean
    ExpenseService expenseService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    void getById_NotFound() throws Exception {
        given(expenseService.findById(any(Long.class))).willReturn(Optional.empty());

        mockMvc.perform(get("/expenses/{id}", new Random().nextLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById() throws Exception {
        ExpenseDTO expense = ExpenseDTO.builder()
                .id(123L)
                .title("test title")
                .deleted(false)
                .amount(BigDecimal.valueOf(123.92))
                .build();

        given(expenseService.findById(123L)).willReturn(Optional.of(expense));

        mockMvc.perform(get("/expenses/{id}", expense.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(123)))
                .andExpect(jsonPath("$.title", is("test title")))
                .andExpect(jsonPath("$.deleted", is(false)))
                .andExpect(jsonPath("$.amount", is(123.92)));
    }

    @Test
    void deleteById() throws Exception {
        Long expenseId = new Random().nextLong();

        given(expenseService.deleteById(any())).willReturn(true);

        mockMvc.perform(delete("/expenses/{id}", expenseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(expenseService).deleteById(captor.capture());

        assertThat(expenseId).isEqualTo(captor.getValue());
    }
}
