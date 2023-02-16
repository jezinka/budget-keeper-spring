package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.YearlyFilterDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.repository.CategoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("")
    List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @PostMapping("/getActiveForSelectedYear")
    List<Category> getActiveForSelectedYear(@RequestBody YearlyFilterDTO yearlyFilter) {
        return categoryRepository.findActiveForYear(yearlyFilter.getYear());
    }
}
