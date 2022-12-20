package com.huyeon.frontend.controller;

import com.huyeon.frontend.aop.RequestAop;
import com.huyeon.frontend.util.Fetch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/workspace/{groupUrl}")
public class CategoryController {

    private final Fetch fetch;

    @GetMapping("/new-category")
    public String createCategoryPage(
            @CookieValue(RequestAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            @PathVariable String groupUrl,
            Model model) {
        fetch.bindResponse(
                "/workspace/" + groupUrl + "/new-category",
                newAccessToken, model
        );
        return "pages/category/newcategory";
    }

    @GetMapping("/category")
    public String manageCategoryPage(
            @CookieValue(RequestAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            @PathVariable String groupUrl,
            Model model) {
        fetch.bindResponse(
                "/workspace/" + groupUrl + "/category",
                newAccessToken, model
        );
        return "pages/category/categorymanage";
    }


    @GetMapping("/{categoryName}")
    public String categoryPage(
            @CookieValue(RequestAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            @PathVariable String groupUrl,
            @PathVariable String categoryName,
            Model model) {
        fetch.bindResponse(
                "/workspace/" + groupUrl + "/" + categoryName,
                newAccessToken, model
        );
        return "pages/category/category";
    }
}
