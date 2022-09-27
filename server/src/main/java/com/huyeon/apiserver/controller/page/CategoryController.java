package com.huyeon.apiserver.controller.page;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.BoardResDto;
import com.huyeon.apiserver.model.dto.CategoryDto;
import com.huyeon.apiserver.model.dto.ContentDto;
import com.huyeon.apiserver.model.entity.Category;
import com.huyeon.apiserver.model.entity.WorkGroup;
import com.huyeon.apiserver.service.BoardService;
import com.huyeon.apiserver.service.CategoryService;
import com.huyeon.apiserver.service.ContentBlockService;
import com.huyeon.apiserver.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/workspace/{groupUrl}")
@RequiredArgsConstructor
public class CategoryController {
    private final GroupService groupService;
    private final CategoryService categoryService;
    private final BoardService boardService;
    private final ContentBlockService blockService;

    @GetMapping("/new-category")
    public String createCategoryPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String groupUrl,
            Model model) {
        model.addAttribute("categoryOptions", categoryOptions(groupUrl));
        return "pages/category/newcategory";
    }

    private List<CategoryDto> categoryOptions(String groupUrl) {
        WorkGroup group = groupService.getGroupByUrl(groupUrl);
        return categoryService.getCategoryList(group);
    }

    @GetMapping("/category")
    public String manageCategoryPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String groupUrl,
            Model model) {
        model.addAttribute("rootCategory", categoryTree(groupUrl));
        return "pages/category/categorymanage";
    }

    private CategoryDto categoryTree(String groupUrl) {
        WorkGroup group = groupService.getGroupByUrl(groupUrl);
        return categoryService.getRootOfCategoryTree(group);
    }

    @GetMapping("/{categoryName}")
    public String categoryPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String groupUrl,
            @PathVariable String categoryName,
            Model model
    ) {
        Category category = categoryService.getCategory(categoryName);

        List<BoardResDto> boards = boardService.getBoardResponsesByCategory(category);

        boards.forEach(board -> {
            List<ContentDto> contents = blockService.getSummaryContentResByBoardId(board.getId());
            board.setContents(contents);
            board.setUrl("/workspace/" + groupUrl + "/board/" + board.getId());
        });

        model.addAttribute("categoryName", categoryName);
        model.addAttribute("boards", boards);
        return "pages/category/category";
    }
}
