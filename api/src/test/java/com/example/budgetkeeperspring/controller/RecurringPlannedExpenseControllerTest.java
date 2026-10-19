package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.RecurringPlannedExpenseDTO;
import com.example.budgetkeeperspring.service.PlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecurringPlannedExpenseController.class)
class RecurringPlannedExpenseControllerTest {

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
    void createReturns201() throws Exception {
        given(planService.createRecurringPayment(any())).willReturn(new RecurringPlannedExpenseDTO());

        mockMvc.perform(post("/recurringPlannedExpenses").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Czynsz\",\"amount\":2000,\"dueDay\":10}"))
                .andExpect(status().isCreated());
    }

    @Test
    void createRejectsDueDayOutsideMonth() throws Exception {
        mockMvc.perform(post("/recurringPlannedExpenses").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Czynsz\",\"amount\":2000,\"dueDay\":32}"))
                .andExpect(status().isBadRequest());
        verify(planService, never()).createRecurringPayment(any());
    }

    @Test
    void deleteReturns204() throws Exception {
        mockMvc.perform(delete("/recurringPlannedExpenses/3")).andExpect(status().isNoContent());
        verify(planService).deleteRecurringPayment(3);
    }
}
