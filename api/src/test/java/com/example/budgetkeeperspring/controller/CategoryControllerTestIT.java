package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.CategoryDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.repository.CategoryRepository;
import com.example.budgetkeeperspring.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CategoryControllerTestIT {

    @Autowired
    CategoryController categoryController;

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void category_notFound() {
        assertThrows(NotFoundException.class, () -> {
            categoryController.getById(new Random().nextLong());
        });
    }

    @Test
    void testGetById() {
        categoryService.saveCategory(CategoryDTO.builder().name("name").build());

        Category category = categoryRepository.findAll().get(0);

        CategoryDTO dto = categoryController.getById(category.getId());

        assertThat(dto).isNotNull();
    }

    @Test
    void findAll_checkOrder() {

        categoryRepository.save(Category.builder().name("testA").build());
        categoryRepository.save(Category.builder().name("Btest").build());

        List<CategoryDTO> result = categoryService.getAll();

        assertEquals(2, result.size());
        assertEquals("Btest", result.get(0).getName());
    }
}