package com.example.budgetkeeperspring.category;

import com.example.budgetkeeperspring.YearlyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("categories")
public class CategoryController {

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("")
    List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @PostMapping("/getActiveForSelectYear")
    List<Category> getActiveForSelectedYear(@RequestBody YearlyFilter yearlyFilter) {
        return categoryRepository.findActiveForYear(yearlyFilter.getYear());
    }
}
