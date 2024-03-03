package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.CategoryCondition;
import com.example.budgetkeeperspring.mapper.CategoryMapper;
import com.example.budgetkeeperspring.repository.CategoryConditionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryConditionRepository categoryConditionRepository;

    @Autowired
    @InjectMocks
    CategoryService categoryService;

    @Spy
    CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    @BeforeEach
    void setup() {
        when(categoryConditionRepository
                .findAll())
                .thenReturn(new ArrayList<>(
                        Arrays.asList(CategoryCondition.builder().conditions("{\"title\":\"apteka\"}").category(Category.builder().name("zdrowie").build()).build(),
                                CategoryCondition.builder().conditions("{\"payee\":\"szkolna kasa\"}").category(Category.builder().name("szkoła").build()).build()))
                );
    }

    @Test
    void findCategoryByConditions_title() {
        Category category = categoryService.findCategoryByConditions(ExpenseDTO.builder().title("Apteka Zielona").build());
        assertEquals("zdrowie", category.getName());
    }

    @Test
    void findCategoryByConditions_who() {
        Category category = categoryService.findCategoryByConditions(ExpenseDTO.builder().payee("Wpłata Szkolna Kasa").title("Wycieczka").build());
        assertEquals("szkoła", category.getName());
    }

    @Test
    void findCategoryByConditions_empty() {
        Category category = categoryService.findCategoryByConditions(ExpenseDTO.builder().title("Ubezpieczenie").build());
        assertNull(category);
    }
}