package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.PlannedExpenseDTO;
import com.example.budgetkeeperspring.service.PlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlannedExpenseController.class)
class PlannedExpenseControllerTest {

    @MockitoBean
    PlanService planService;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void createRejectsNonPositiveAmount() throws Exception {
        mockMvc.perform(post("/plannedExpenses").contentType(MediaType.APPLICATION_JSON).content("{\"planId\":1,\"amount\":0}"))
                .andExpect(status().isBadRequest());
        verify(planService, never()).createPlannedExpense(any());
    }

    @Test
    void createReturns201() throws Exception {
        PlannedExpenseDTO created = new PlannedExpenseDTO();
        created.setId(5);
        given(planService.createPlannedExpense(any())).willReturn(created);

        mockMvc.perform(post("/plannedExpenses").contentType(MediaType.APPLICATION_JSON).content("{\"planId\":1,\"amount\":10.5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(5)));
    }

    @Test
    void marksAsPaid() throws Exception {
        PlannedExpenseDTO paid = new PlannedExpenseDTO();
        paid.setPaid(true);
        given(planService.updatePaid(5, true)).willReturn(paid);

        mockMvc.perform(patch("/plannedExpenses/5/paid").param("paid", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paid", is(true)));
    }

    @Test
    void assignsExpensesAndReturns204() throws Exception {
        mockMvc.perform(post("/plannedExpenses/5/expenses").contentType(MediaType.APPLICATION_JSON).content("[10,11]"))
                .andExpect(status().isNoContent());
        verify(planService).assignExpensesToPlannedExpense(5, List.of(10L, 11L));
    }

    @Test
    void assigningAlreadyPlannedExpenseIsConflict() throws Exception {
        willThrow(new ResponseStatusException(HttpStatus.CONFLICT)).given(planService).assignExpenseToPlannedExpense(5, 10L);

        mockMvc.perform(post("/plannedExpenses/5/expenses/10")).andExpect(status().isConflict());
    }

    @Test
    void unassignsExpense() throws Exception {
        mockMvc.perform(delete("/plannedExpenses/5/expenses/10")).andExpect(status().isNoContent());
        verify(planService).unassignExpenseFromPlannedExpense(5, 10L);
    }
}
