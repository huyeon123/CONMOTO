package com.huyeon.apiserver.controller.api;

import com.huyeon.apiserver.model.dto.CategoryDto;
import com.huyeon.apiserver.model.entity.WorkGroup;
import com.huyeon.apiserver.service.CategoryService;
import com.huyeon.apiserver.service.GroupService;
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
    private final GroupService groupService;
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestBody CategoryDto request,
            @RequestParam String groupUrl) {
        WorkGroup group = groupService.getGroupByUrl(groupUrl);
        boolean success = categoryService.createCategory(request, group);

        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> editCategory(
            @RequestBody List<CategoryDto> request,
            @RequestParam String groupUrl) {
        WorkGroup group = groupService.getGroupByUrl(groupUrl);
        boolean success = categoryService.editCategory(request, group);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }
}
