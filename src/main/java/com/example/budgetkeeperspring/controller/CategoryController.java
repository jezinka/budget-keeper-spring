package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.YearlyFilterDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping("")
    List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @PostMapping("/getActiveForSelectedYear")
    List<Category> getActiveForSelectedYear(@RequestBody YearlyFilterDTO yearlyFilter) {
        return categoryRepository.findActiveForYear(yearlyFilter.getYear());
    }
}
