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

    private final CategoryLevelRepository categoryLevelRepository;

    public Map<Integer, String> getCategoryLevels() {
        return categoryLevelRepository.findAll()
                .stream()
                .collect(toMap(CategoryLevel::getLevel, CategoryLevel::getName));
    }
}
