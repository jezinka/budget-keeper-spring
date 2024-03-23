package com.example.budgetkeeperspring.controller;

import com.example.budgetkeeperspring.dto.CategoryDTO;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("")
    List<CategoryDTO> getAll() {
        return categoryService.getAll();
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
}
