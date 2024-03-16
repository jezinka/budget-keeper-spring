package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.CategoryCondition;
import com.example.budgetkeeperspring.entity.Circ;
import com.example.budgetkeeperspring.mapper.CategoryMapper;
import com.example.budgetkeeperspring.repository.CategoryConditionRepository;
import org.junit.jupiter.api.Assertions;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryConditionRepository categoryConditionRepository;

    @InjectMocks
    @Autowired
    CategoryService categoryService;

    @Spy
    CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    @BeforeEach
    void setup() {
        when(categoryConditionRepository
                .findAll())
                .thenReturn(new ArrayList<>(
                        Arrays.asList(CategoryCondition.builder().circ(Circ.builder().title("apteka").build()).category(Category.builder().name("zdrowie").build()).build(),
                                CategoryCondition.builder().circ(Circ.builder().payee("szkolna kasa").build()).category(Category.builder().name("szkoła").build()).build()))
                );
    }

    @Test
    void findCategoryByConditions_title() {
        Optional<Category>  category = categoryService.findCategoryByConditions(ExpenseDTO.builder().title("Apteka Zielona").payee("").build());
        assertTrue(category.isPresent());
        assertEquals("zdrowie", category.get().getName());
    }

    @Test
    void findCategoryByConditions_who() {
        Optional<Category>  category = categoryService.findCategoryByConditions(ExpenseDTO.builder().payee("Wpłata Szkolna Kasa").title("Wycieczka").build());
        assertTrue(category.isPresent());
        assertEquals("szkoła", category.get().getName());
    }

    @Test
    void findCategoryByConditions_empty() {
        Optional<Category> category = categoryService.findCategoryByConditions(ExpenseDTO.builder().title("Ubezpieczenie").payee("").build());
        assertTrue(category.isEmpty());
    }
}