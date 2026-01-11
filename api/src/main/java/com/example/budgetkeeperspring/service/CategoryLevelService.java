package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.entity.CategoryLevel;
import com.example.budgetkeeperspring.repository.CategoryLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class CategoryLevelService {

    public static final Integer INVESTMENT_CATEGORY_LEVEL = 2;
    public static final Integer INCOME_CATEGORY_LEVEL = 4;
    public static final Integer MORTGAGE_CATEGORY_LEVEL = 5;

    private final CategoryLevelRepository categoryLevelRepository;

    public Map<Integer, String> getCategoryLevels() {
        return categoryLevelRepository.findAll()
                .stream()
                .collect(toMap(CategoryLevel::getLevel, CategoryLevel::getName));
    }
}
