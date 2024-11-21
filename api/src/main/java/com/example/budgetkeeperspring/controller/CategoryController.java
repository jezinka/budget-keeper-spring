package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.CategoryDTO;
import com.example.budgetkeeperspring.dto.CategoryLevelDTO;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/all")
    List<CategoryDTO> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/onlyExpenses")
    List<CategoryDTO> getOnlyExpenses() {
        return categoryService.getOnlyExpenses();
    }

    @GetMapping("/getActiveForSelectedYear/{year}")
    List<CategoryDTO> getActiveForSelectedYear(@PathVariable("year") Integer year) {
        return categoryService.findActiveForYear(year);
    }

    @PostMapping("")
    ResponseEntity<Void> add(@Validated @RequestBody CategoryDTO categoryDTO) {

        CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "categories/" + savedCategory.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    CategoryDTO getById(@PathVariable Long id) {
        return categoryService.findById(id).orElseThrow(NotFoundException::new);
    }

    @GetMapping("/levels")
    List<CategoryLevelDTO> getLevels() {
        return categoryService.getAllLevels();
    }
}
