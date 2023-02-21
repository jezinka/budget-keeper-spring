package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

import static com.example.budgetkeeperspring.controller.ExpenseController.EXPENSES_PATH_ID;
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

    @Test
    void getById_NotFound() throws Exception {
        given(expenseService.findById(any(Long.class))).willReturn(Optional.empty());

        mockMvc.perform(get(EXPENSES_PATH_ID, new Random().nextLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById() throws Exception {
        ExpenseDTO expense = new ExpenseDTO().builder()
                .id(123L)
                .title("test title")
                .deleted(false)
                .amount(BigDecimal.valueOf(123.92))
                .build();

        given(expenseService.findById(123L)).willReturn(Optional.of(expense));

        mockMvc.perform(get(EXPENSES_PATH_ID, expense.getId())
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

        mockMvc.perform(delete(EXPENSES_PATH_ID, expenseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(expenseService).deleteById(captor.capture());

        assertThat(expenseId).isEqualTo(captor.getValue());
    }
}
