package com.huyeon.superspace.domain.newboard.controller;

import com.huyeon.superspace.domain.newboard.dto.CategoryDto;
import com.huyeon.superspace.domain.newboard.service.NewCategoryService;
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
    public List<CategoryDto> getCategories(@RequestParam String groupUrl) {
        return categoryService.getCategoryTree(groupUrl);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createCategory(@RequestBody CategoryDto request) {
        return categoryService.createCategory(request);
    }

    @PutMapping
    public void editCategory(
            @RequestBody List<CategoryDto> request,
            @RequestParam String groupUrl) {
        categoryService.editCategory(request, groupUrl);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
    }
}
