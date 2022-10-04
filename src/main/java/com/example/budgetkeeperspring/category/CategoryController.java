package com.example.budgetkeeperspring.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("categories")
public class CategoryController {

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("")
    List<Category> getAll() {
        return categoryRepository.getAll();
    }

    @GetMapping("/getActiveForCurrentYear")
    List<Category> getActiveForCurrentYear() {
        int year = Year.now().getValue();
        return categoryRepository.getActive(year);
    }
}
