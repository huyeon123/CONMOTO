package com.huyeon.superspace.web.domain.board;

import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.board.dto.ContentDto;
import com.huyeon.superspace.domain.board.entity.Category;
import com.huyeon.superspace.domain.board.service.BoardService;
import com.huyeon.superspace.domain.board.service.CategoryService;
import com.huyeon.superspace.domain.board.service.ContentBlockService;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.group.service.GroupService;
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
@RequestMapping("/api/workspace/{groupUrl}")
@RequiredArgsConstructor
public class CategoryController {
    private final GroupService groupService;
    private final CategoryService categoryService;
    private final BoardService boardService;
    private final ContentBlockService blockService;

    @GroupPage
    @ManagerPage
    @GetMapping("/new-category")
    public Map<String, Object> createCategoryPage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl) {
        Map<String, Object> response = new HashMap<>();
        response.put("categoryOptions", categoryOptions(groupUrl));

        return response;
    }

    private List<CategoryDto> categoryOptions(String groupUrl) {
        WorkGroup group = groupService.getGroupByUrl(groupUrl);
        return categoryService.getCategoryList(group);
    }

    @GroupPage
    @ManagerPage
    @GetMapping("/category")
    public Map<String, Object> manageCategoryPage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl) {
        Map<String, Object> response = new HashMap<>();
        response.put("topCategories", getCategoryTree(groupUrl));

        return response;
    }

    private List<CategoryDto> getCategoryTree(String groupUrl) {
        WorkGroup group = groupService.getGroupByUrl(groupUrl);
        return categoryService.getCategoryTree(group);
    }

    @GroupPage
    @GetMapping("/{categoryName}")
    public Map<String, Object> categoryPage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl,
            @PathVariable String categoryName) {
        Map<String, Object> response = new HashMap<>();

        Category category = categoryService.getCategory(categoryName);

        List<BoardDto> boards = boardService.getBoardResponsesByCategory(category);

        boards.forEach(board -> {
            List<ContentDto> contents = blockService.getSummaryContentResByBoardId(board.getId());
            board.setContents(contents);
            board.setUrl("/workspace/" + groupUrl + "/board/" + board.getId());
        });

        response.put("categoryName", categoryName);
        response.put("boards", boards);
        return response;
    }
}
