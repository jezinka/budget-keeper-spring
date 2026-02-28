package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.CategoryDTO;
import com.example.budgetkeeperspring.dto.CategoryLevelDTO;
import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.CategoryCondition;
import com.example.budgetkeeperspring.entity.CategoryLevel;
import com.example.budgetkeeperspring.entity.Circ;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.mapper.CategoryLevelMapper;
import com.example.budgetkeeperspring.mapper.CategoryMapper;
import com.example.budgetkeeperspring.repository.CategoryConditionRepository;
import com.example.budgetkeeperspring.repository.CategoryLevelRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryLevelRepository categoryLevelRepository;

    @Mock
    private CategoryConditionRepository categoryConditionRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryLevelMapper categoryLevelMapper;

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

    @Test
    void getOnlyExpenses_excludesIncomeCategories() {
        Category expenseCategory = new Category();
        expenseCategory.setId(2L);
        expenseCategory.setName("Food");
        expenseCategory.setLevel(1);

        Category incomeCategory = new Category();
        incomeCategory.setId(3L);
        incomeCategory.setName("Salary");
        incomeCategory.setLevel(4);

        CategoryLevel incomeLevel = new CategoryLevel();
        incomeLevel.setLevel(4);
        incomeLevel.setName("Wpływy");

        CategoryDTO expenseDTO = new CategoryDTO();
        expenseDTO.setId(2L);
        expenseDTO.setName("Food");

        when(categoryLevelRepository.findAllByNameIn(List.of("Wpływy", "Kredyt")))
                .thenReturn(List.of(incomeLevel));
        when(categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name")))
                .thenReturn(List.of(expenseCategory, incomeCategory));
        when(categoryMapper.mapToDto(expenseCategory)).thenReturn(expenseDTO);

        List<CategoryDTO> result = categoryService.getOnlyExpenses();

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getName());
    }

    @Test
    void getOnlyExpenses_includeCategoriesWithNullLevel() {
        Category categoryWithoutLevel = new Category();
        categoryWithoutLevel.setId(1L);
        categoryWithoutLevel.setName("Uncategorized");
        categoryWithoutLevel.setLevel(null);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Uncategorized");

        when(categoryLevelRepository.findAllByNameIn(List.of("Wpływy", "Kredyt")))
                .thenReturn(List.of());
        when(categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name")))
                .thenReturn(List.of(categoryWithoutLevel));
        when(categoryMapper.mapToDto(categoryWithoutLevel)).thenReturn(categoryDTO);

        List<CategoryDTO> result = categoryService.getOnlyExpenses();

        assertEquals(1, result.size());
        assertEquals("Uncategorized", result.get(0).getName());
    }

    @Test
    void findActiveForYear_filtersOutCategoriesNotUsedInCharts() {
        Category chartCategory = new Category();
        chartCategory.setId(1L);
        chartCategory.setName("Active");
        chartCategory.setUseInYearlyCharts(true);

        Category inactiveCategory = new Category();
        inactiveCategory.setId(2L);
        inactiveCategory.setName("Inactive");
        inactiveCategory.setUseInYearlyCharts(false);

        CategoryDTO chartCategoryDTO = new CategoryDTO();
        chartCategoryDTO.setId(1L);
        chartCategoryDTO.setName("Active");

        when(categoryRepository.findActiveForYear(2024)).thenReturn(List.of(chartCategory, inactiveCategory));
        when(categoryMapper.mapToDto(chartCategory)).thenReturn(chartCategoryDTO);

        List<CategoryDTO> result = categoryService.findActiveForYear(2024);

        assertEquals(1, result.size());
        assertEquals("Active", result.get(0).getName());
    }

    @Test
    void getAllLevels_returnsAllCategoryLevels() {
        CategoryLevel level1 = new CategoryLevel();
        level1.setLevel(1);
        level1.setName("Expenses");

        CategoryLevel level2 = new CategoryLevel();
        level2.setLevel(2);
        level2.setName("Income");

        CategoryLevelDTO dto1 = new CategoryLevelDTO();
        dto1.setLevel("1");
        dto1.setName("Expenses");

        CategoryLevelDTO dto2 = new CategoryLevelDTO();
        dto2.setLevel("2");
        dto2.setName("Income");

        when(categoryLevelRepository.findAll()).thenReturn(List.of(level1, level2));
        when(categoryLevelMapper.mapToDto(level1)).thenReturn(dto1);
        when(categoryLevelMapper.mapToDto(level2)).thenReturn(dto2);

        List<CategoryLevelDTO> result = categoryService.getAllLevels();

        assertEquals(2, result.size());
        assertEquals("Expenses", result.get(0).getName());
        assertEquals("Income", result.get(1).getName());
    }

    @Test
    void findCategoryByConditions_matchesWhenOnlyTitleIsCondition() {
        CategoryCondition condition = new CategoryCondition();
        condition.setCategory(category);
        Circ circ = new Circ();
        circ.setTitle("expense");
        circ.setPayee(null);
        condition.setCirc(circ);

        when(categoryConditionRepository.findAll()).thenReturn(List.of(condition));

        Category result = categoryService.findCategoryByConditions(expenseDTO);

        assertEquals("Test Category", result.getName());
    }

    @Test
    void findCategoryByConditions_matchesWhenOnlyPayeeIsCondition() {
        CategoryCondition condition = new CategoryCondition();
        condition.setCategory(category);
        Circ circ = new Circ();
        circ.setTitle(null);
        circ.setPayee("payee");
        condition.setCirc(circ);

        when(categoryConditionRepository.findAll()).thenReturn(List.of(condition));

        Category result = categoryService.findCategoryByConditions(expenseDTO);

        assertEquals("Test Category", result.getName());
    }

    @Test
    void findCategoryByConditions_returnsDefaultCategoryWhenNoConditionsMatch() {
        CategoryCondition condition = new CategoryCondition();
        condition.setCategory(category);
        Circ circ = new Circ();
        circ.setTitle("other");
        circ.setPayee("other");
        condition.setCirc(circ);

        Category defaultCategory = new Category();
        defaultCategory.setId(CategoryService.UNKNOWN_CATEGORY);
        defaultCategory.setName("Unknown");

        when(categoryConditionRepository.findAll()).thenReturn(List.of(condition));
        when(categoryRepository.findById(CategoryService.UNKNOWN_CATEGORY))
                .thenReturn(Optional.of(defaultCategory));

        Category result = categoryService.findCategoryByConditions(expenseDTO);

        assertEquals("Unknown", result.getName());
    }
}
