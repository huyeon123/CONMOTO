package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.model.dto.CategoryDto;
import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.service.CategoryService;
import com.huyeon.apiserver.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/new-category")
    public String createCategoryPage(
            @PathVariable String groupUrl,
            Model model) {
        model.addAttribute("categoryOptions", categoryOptions(groupUrl));
        return "categorypage";
    }

    @GetMapping("/category")
    public String manageCategoryPage(
            @PathVariable String groupUrl,
            Model model) {
        model.addAttribute("rootCategory", categoryTree(groupUrl));
        return "categorymanage";
    }

    public List<CategoryDto> categoryOptions(String groupUrl) {
        Groups group = groupService.getGroupByUrl(groupUrl);
        return categoryService.getCategories(group);
    }

    public CategoryDto categoryTree(String groupUrl) {
        Groups group = groupService.getGroupByUrl(groupUrl);
        return categoryService.getRootOfCategoryTree(group);
    }
}
