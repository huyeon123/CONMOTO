package com.huyeon.superspace.web.domain.board;

import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.board.service.NewCategoryService;
import com.huyeon.superspace.web.annotation.GroupPage;
import com.huyeon.superspace.web.annotation.ManagerPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/community/{groupUrl}")
@RequiredArgsConstructor
public class CategoryController {
    private final NewCategoryService categoryService;

    @GroupPage
    @ManagerPage
    @GetMapping("/manage/category")
    public Map<String, Object> manageCategoryPage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("categories", getCategoryList(groupUrl));

        return response;
    }

    private List<CategoryDto> getCategoryList(String groupUrl) {
        return categoryService.getCategoryListByUrl(groupUrl);
    }

    @GroupPage
    @GetMapping("/{categoryName}")
    public Map<String, Object> categoryPage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl,
            @PathVariable String categoryName
    ) {
        Map<String, Object> response = new HashMap<>();
        categoryName = categoryName.replace("_", " ");
        CategoryDto category = categoryService.getCategoryByName(groupUrl, categoryName);
        response.put("category", category);
        return response;
    }
}
