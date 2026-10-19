package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.PlanDTO;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.service.PlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanController.class)
class PlanControllerTest {

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
    void createReturns201WithCreatedPlan() throws Exception {
        PlanDTO created = new PlanDTO();
        created.setId(7);
        created.setYear(2026);
        created.setMonth(10);
        given(planService.create(any())).willReturn(created);

        mockMvc.perform(post("/plans").contentType(MediaType.APPLICATION_JSON).content("{\"year\":2026,\"month\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(7)));
    }

    @Test
    void createRejectsInvalidMonth() throws Exception {
        mockMvc.perform(post("/plans").contentType(MediaType.APPLICATION_JSON).content("{\"year\":2026,\"month\":13}"))
                .andExpect(status().isBadRequest());
        verify(planService, never()).create(any());
    }

    @Test
    void createPassesConflictFromService() throws Exception {
        given(planService.create(any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        mockMvc.perform(post("/plans").contentType(MediaType.APPLICATION_JSON).content("{\"year\":2026,\"month\":10}"))
                .andExpect(status().isConflict());
    }

    @Test
    void missingPlanIs404() throws Exception {
        given(planService.findById(99)).willThrow(new NotFoundException());

        mockMvc.perform(get("/plans/99")).andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns204() throws Exception {
        mockMvc.perform(delete("/plans/7")).andExpect(status().isNoContent());
        verify(planService).delete(7);
    }

    @Test
    void createsPlannedExpenseFromSelectedExpenses() throws Exception {
        mockMvc.perform(post("/plans/7/plannedExpenses/fromExpenses").contentType(MediaType.APPLICATION_JSON).content("[1,2]"))
                .andExpect(status().isOk());
        verify(planService).createPlannedExpenseFromExpenses(7, List.of(1L, 2L));
    }

    @Test
    void emptyUploadIsRejectedWithoutImport() throws Exception {
        mockMvc.perform(multipart("/plans/7/upload").file(new MockMultipartFile("file", new byte[0])))
                .andExpect(status().isBadRequest());
        verify(planService, never()).importPlannedExpenses(any(), any());
    }

    @Test
    void summaryTakesYearAndMonth() throws Exception {
        mockMvc.perform(get("/plans/summary").param("year", "2026").param("month", "10")).andExpect(status().isOk());
        verify(planService).summary(2026, 10);
    }
}
