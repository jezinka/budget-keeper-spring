package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.CategoryDTO;
import com.example.budgetkeeperspring.dto.CategoryLevelDTO;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Categories", description = "Operations for managing categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories")
    @GetMapping("/all")
    List<CategoryDTO> getAll() {
        return categoryService.getAll();
    }

    @Operation(summary = "Get only expense categories")
    @GetMapping("/onlyExpenses")
    List<CategoryDTO> getOnlyExpenses() {
        return categoryService.getOnlyExpenses();
    }

    @Operation(summary = "Get active categories for a selected year")
    @GetMapping("/getActiveForSelectedYear/{year}")
    List<CategoryDTO> getActiveForSelectedYear(@PathVariable("year") Integer year) {
        return categoryService.findActiveForYear(year);
    }

    @Operation(summary = "Create a new category")
    @PostMapping("")
    ResponseEntity<Void> add(@Validated @RequestBody CategoryDTO categoryDTO) {

        CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "categories/" + savedCategory.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(summary = "Get a category by ID")
    @GetMapping("/{id}")
    CategoryDTO getById(@PathVariable Long id) {
        return categoryService.findById(id).orElseThrow(NotFoundException::new);
    }

    @Operation(summary = "Get all category levels")
    @GetMapping("/levels")
    List<CategoryLevelDTO> getLevels() {
        return categoryService.getAllLevels();
    }
}
