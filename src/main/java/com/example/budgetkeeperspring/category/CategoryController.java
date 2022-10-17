package com.example.budgetkeeperspring.category;

import com.example.budgetkeeperspring.YearlyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/getActiveForSelectYear")
    List<Category> getActiveForSelectedYear(@RequestBody YearlyFilter yearlyFilter) {
        return categoryRepository.getActive(yearlyFilter.getYear());
    }
}
