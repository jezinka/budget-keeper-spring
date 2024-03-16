package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.CategoryDTO;
import com.example.budgetkeeperspring.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @MockBean
    CategoryService categoryService;

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
        given(categoryService.findById(any(Long.class))).willReturn(Optional.empty());

        mockMvc.perform(get("/categories/{id}", new Random().nextLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById() throws Exception {
        CategoryDTO dto = CategoryDTO.builder()
                .id(123L)
                .name("name")
                .build();

        given(categoryService.findById(123L)).willReturn(Optional.of(dto));

        mockMvc.perform(get("/categories/{id}", dto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(123)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.useInYearlyCharts", is(true)));
    }
}
