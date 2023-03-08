package com.huyeon.superspace.web.domain.board;

import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.board.service.NewBoardService;
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
    private final NewBoardService boardService;

    @GroupPage
    @ManagerPage
    @GetMapping("/new-category")
    public Map<String, Object> createCategoryPage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("categoryOptions", categoryOptions(groupUrl));

        return response;
    }

    private List<CategoryDto> categoryOptions(String groupUrl) {
        return categoryService.getCategoryList(groupUrl);
    }

    @GroupPage
    @ManagerPage
    @GetMapping("/category")
    public Map<String, Object> manageCategoryPage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("topCategories", getCategoryTree(userEmail, groupUrl));

        return response;
    }

    private List<CategoryDto> getCategoryTree(String email, String groupUrl) {
        return categoryService.getCategoryTree(email, groupUrl);
    }

    @GroupPage
    @GetMapping("/{categoryName}")
    public Map<String, Object> categoryPage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl,
            @PathVariable String categoryName
    ) {
        Map<String, Object> response = new HashMap<>();

        //TODO: 존재하지 않는 CategoryName이면 400에러

        List<BoardDto> boards = boardService.getNext10LatestInCategory(categoryName, 0);

        response.put("categoryName", categoryName);
        response.put("boards", boards);
        return response;
    }
}
