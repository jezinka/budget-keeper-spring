package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.CategoryDTO;
import com.example.budgetkeeperspring.dto.ConditionDTO;
import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.CategoryCondition;
import com.example.budgetkeeperspring.mapper.CategoryMapper;
import com.example.budgetkeeperspring.repository.CategoryConditionRepository;
import com.example.budgetkeeperspring.repository.CategoryRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryConditionRepository categoryConditionRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDTO> getAll() {
        return categoryRepository
                .findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(categoryMapper::mapToDto)
                .toList();
    }

    public List<CategoryDTO> findActiveForYear(int year) {
        return categoryRepository.findActiveForYear(year)
                .stream()
                .filter(Category::isUseInYearlyCharts)
                .map(categoryMapper::mapToDto)
                .toList();
    }

    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        return categoryMapper.mapToDto(categoryRepository.save(categoryMapper.mapToEntity(categoryDTO)));
    }

    public Optional<CategoryDTO> findById(Long id) {
        return Optional.ofNullable(categoryMapper.mapToDto(categoryRepository.findById(id)
                .orElse(null)));
    }

    public Category findCategoryByConditions(ExpenseDTO expenseDTO) {
        List<CategoryCondition> categoryConditions = categoryConditionRepository.findAll();
        Gson g = new Gson();

        String title = expenseDTO.getTitle().toLowerCase();
        String payee = expenseDTO.getPayee().toLowerCase();

        AtomicReference<Category> category = new AtomicReference<>();

        for (CategoryCondition cc : categoryConditions) {
            if (cc.getConditions() != null) {
                ConditionDTO c = g.fromJson(cc.getConditions(), ConditionDTO.class);
                if (c.getTitle() != null && title.contains(c.getTitle()) || c.getPayee() != null && payee.contains(c.getPayee())) {
                    category.set(cc.getCategory());
                    break;
                }
            }
        }

        return category.get();
    }
}
