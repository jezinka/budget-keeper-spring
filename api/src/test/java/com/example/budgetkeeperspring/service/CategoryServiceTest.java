package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.CategoryDTO;
import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.CategoryCondition;
import com.example.budgetkeeperspring.entity.Circ;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.mapper.CategoryMapper;
import com.example.budgetkeeperspring.repository.CategoryConditionRepository;
import com.example.budgetkeeperspring.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryConditionRepository categoryConditionRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;
    private ExpenseDTO expenseDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Test Category");

        expenseDTO = new ExpenseDTO();
        expenseDTO.setTitle("Test Expense");
        expenseDTO.setPayee("Test Payee");
    }

    @Test
    void getAll_returnsAllCategoriesSortedByName() {
        when(categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(List.of(category));
        when(categoryMapper.mapToDto(category)).thenReturn(categoryDTO);

        List<CategoryDTO> result = categoryService.getAll();

        assertEquals(1, result.size());
        assertEquals("Test Category", result.get(0).getName());
    }

    @Test
    void findActiveForYear_returnsActiveCategoriesForYear() {
        when(categoryRepository.findActiveForYear(2024)).thenReturn(List.of(category));
        when(categoryMapper.mapToDto(category)).thenReturn(categoryDTO);

        List<CategoryDTO> result = categoryService.findActiveForYear(2024);

        assertEquals(1, result.size());
        assertEquals("Test Category", result.get(0).getName());
    }

    @Test
    void saveCategory_savesAndReturnsCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.mapToEntity(categoryDTO)).thenReturn(category);
        when(categoryMapper.mapToDto(category)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.saveCategory(categoryDTO);

        assertEquals("Test Category", result.getName());
    }

    @Test
    void findById_returnsCategoryIfExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.mapToDto(category)).thenReturn(categoryDTO);

        Optional<CategoryDTO> result = categoryService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Category", result.get().getName());
    }

    @Test
    void findById_returnsEmptyIfCategoryDoesNotExist() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<CategoryDTO> result = categoryService.findById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findCategoryByConditions_returnsCategoryIfConditionsMatch() {
        CategoryCondition condition = new CategoryCondition();
        condition.setCategory(category);
        Circ circ = new Circ();
        circ.setTitle("test expense");
        circ.setPayee("test payee");
        condition.setCirc(circ);

        when(categoryConditionRepository.findAll()).thenReturn(List.of(condition));

        Category result = categoryService.findCategoryByConditions(expenseDTO);

        assertEquals("Test Category", result.getName());
    }

    @Test
    void findCategoryByConditions_returnsEmptyIfNoConditionsMatch() {
        when(categoryConditionRepository.findAll()).thenReturn(List.of());
        assertThrows(NotFoundException.class, () -> {
            categoryService.findCategoryByConditions(expenseDTO);
        });
    }
}