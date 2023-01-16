package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.board.service.NewCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryRestController {
    private final NewCategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam String url) {
        return categoryService.getCategoryTree(url);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createCategory(@RequestBody CategoryDto request) {
        return categoryService.createCategory(request);
    }

    @PutMapping
    public void editCategory(
            @RequestBody List<CategoryDto> request,
            @RequestParam String url) {
        categoryService.editCategory(request, url);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
    }
}
