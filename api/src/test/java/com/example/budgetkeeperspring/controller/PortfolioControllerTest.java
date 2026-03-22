package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.FireStageDTO;
import com.example.budgetkeeperspring.dto.PortfolioSnapshotDTO;
import com.example.budgetkeeperspring.service.FireStageService;
import com.example.budgetkeeperspring.service.PortfolioSnapshotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.example.budgetkeeperspring.controller.PortfolioController.PATH;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

    @MockBean
    PortfolioSnapshotService portfolioSnapshotService;

    @MockBean
    FireStageService fireStageService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void getSnapshots_returnsListOfDTOs() throws Exception {
        List<PortfolioSnapshotDTO> snapshots = List.of(
                PortfolioSnapshotDTO.builder().id(1L).date(LocalDate.of(2024, 1, 1)).value(new BigDecimal("100000")).build(),
                PortfolioSnapshotDTO.builder().id(2L).date(LocalDate.of(2024, 2, 1)).value(new BigDecimal("120000")).build()
        );
        given(portfolioSnapshotService.findAll()).willReturn(snapshots);

        mockMvc.perform(get(PATH + "/snapshots").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].value", is(100000)));
    }

    @Test
    void addSnapshot_returns201WhenNew() throws Exception {
        PortfolioSnapshotDTO dto = PortfolioSnapshotDTO.builder()
                .date(LocalDate.of(2024, 3, 1)).value(new BigDecimal("150000")).build();
        PortfolioSnapshotDTO saved = PortfolioSnapshotDTO.builder().id(5L).date(dto.getDate()).value(dto.getValue()).build();

        given(portfolioSnapshotService.save(any(), any())).willReturn(saved);

        mockMvc.perform(post(PATH + "/snapshots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(5)));
    }

    @Test
    void addSnapshot_returns409WhenDuplicate() throws Exception {
        PortfolioSnapshotDTO dto = PortfolioSnapshotDTO.builder()
                .date(LocalDate.of(2024, 3, 1)).value(new BigDecimal("150000")).build();

        given(portfolioSnapshotService.save(any(), any())).willReturn(null);

        mockMvc.perform(post(PATH + "/snapshots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void fetchNow_triggersServiceAndReturns200() throws Exception {
        mockMvc.perform(post(PATH + "/snapshots/fetch"))
                .andExpect(status().isOk());

        verify(portfolioSnapshotService).fetchFromMyFundAndSave();
    }

    @Test
    void getFireStages_returnsListOfDTOs() throws Exception {
        List<FireStageDTO> stages = List.of(
                FireStageDTO.builder().id(1L).threshold(new BigDecimal("75000"))
                        .firstCrossedAt(LocalDate.of(2022, 1, 15)).build(),
                FireStageDTO.builder().id(2L).threshold(new BigDecimal("150000")).build()
        );
        given(fireStageService.findAll()).willReturn(stages);

        mockMvc.perform(get(PATH + "/fire-stages").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].threshold", is(75000)))
                .andExpect(jsonPath("$[0].firstCrossedAt", is("2022-01-15")))
                .andExpect(jsonPath("$[1].firstCrossedAt").doesNotExist());
    }
}
