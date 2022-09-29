package com.huyeon.apiserver.controller.api;

import com.huyeon.apiserver.model.dto.CategoryDto;
import com.huyeon.apiserver.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryApiController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestBody CategoryDto request,
            @RequestParam String groupUrl) {
        categoryService.createCategory(request, groupUrl);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> editCategory(
            @RequestBody List<CategoryDto> request,
            @RequestParam String groupUrl) {
        categoryService.editCategory(request, groupUrl);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
