package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.board.dto.FavoriteCategoryReq;
import com.huyeon.superspace.domain.board.service.NewCategoryService;
import com.huyeon.superspace.domain.group.service.NewGroupService;
import com.huyeon.superspace.global.exception.PermissionDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryRestController {
    private final NewCategoryService categoryService;
    private final NewGroupService groupService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam String url) {
        return categoryService.getCategoryListByUrl(url);
    }

    @PostMapping("/save")
    public void saveCategory(
            @RequestParam String url,
            @RequestBody List<CategoryDto> request,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        checkPermission(url, userEmail, "카테고리 수정 권한이 없습니다.");
        categoryService.saveCategory(url, request);
    }

    @PutMapping
    public String editCategory(
            @RequestBody CategoryDto request,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        checkPermission(request.getGroupUrl(), userEmail, "카테고리 수정 권한이 없습니다.");
        return categoryService.editCategory(request);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(
            @PathVariable String id,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        String groupUrl = categoryService.getCategory(id).getGroupUrl();
        checkPermission(groupUrl, userEmail, "카테고리 삭제 권한이 없습니다.");
        categoryService.deleteCategory(id);
    }

    private void checkPermission(String groupUrl, String userEmail, String errorMsg) {
        if (groupService.isNotManager(groupUrl, userEmail)) {
            throw new PermissionDeniedException(errorMsg);
        }
    }

    @PutMapping("/favorite")
    public void setFavoriteCategory(
            @RequestHeader("X-Authorization-Id") String email,
            @RequestBody FavoriteCategoryReq request
    ) {
        categoryService.setFavorite(email, request);
    }
}
