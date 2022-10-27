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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @GroupPage
    @GetMapping("/new-category")
    public String createCategoryPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String groupUrl,
            Model model) {
        model.addAttribute("categoryOptions", categoryOptions(groupUrl));
        return "pages/category/newcategory";
    }

    private List<CategoryDto> categoryOptions(String groupUrl) {
        WorkGroup group = groupService.getGroupByUrl(groupUrl);
        return categoryService.getCategoryList(group);
    }

    @GroupPage
    @GetMapping("/category")
    public String manageCategoryPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String groupUrl,
            Model model) {
        model.addAttribute("rootCategory", categoryTree(groupUrl));
        return "pages/category/categorymanage";
    }

    private CategoryDto categoryTree(String groupUrl) {
        WorkGroup group = groupService.getGroupByUrl(groupUrl);
        return categoryService.getRootOfCategoryTree(group);
    }

    @GroupPage
    @GetMapping("/{categoryName}")
    public String categoryPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String groupUrl,
            @PathVariable String categoryName,
            Model model
    ) {
        Category category = categoryService.getCategory(categoryName);

        List<BoardDto> boards = boardService.getBoardResponsesByCategory(category);

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
