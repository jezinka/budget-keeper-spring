package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.service.BudgetPlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BudgetPlanController.class)
class BudgetPlanControllerTest {

    @MockitoBean
    BudgetPlanService budgetPlanService;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void exportsCsvAsAttachment() throws Exception {
        given(budgetPlanService.writeDataToCsv()).willReturn("Kategoria;Kwota\nJedzenie;100\n");

        mockMvc.perform(get("/budgetPlan/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=budgetPlan.csv"))
                .andExpect(content().string("Kategoria;Kwota\nJedzenie;100\n"));
    }

    @Test
    void emptyUploadIsRejected() throws Exception {
        mockMvc.perform(multipart("/budgetPlan/upload").file(new MockMultipartFile("file", new byte[0])))
                .andExpect(status().isBadRequest());
        verify(budgetPlanService, never()).createGoalsFromFile(any());
    }

    @Test
    void failedImportIs500() throws Exception {
        given(budgetPlanService.createGoalsFromFile(any())).willReturn(false);

        mockMvc.perform(multipart("/budgetPlan/upload").file(new MockMultipartFile("file", "plan.csv", null, new byte[]{1})))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void autoFillDefaultsToThreeMonths() throws Exception {
        given(budgetPlanService.autoFillGoalsFromAverage(3)).willReturn("ok");

        mockMvc.perform(post("/budgetPlan/autoFill"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }
}
