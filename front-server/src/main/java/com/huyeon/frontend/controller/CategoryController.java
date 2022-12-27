package com.huyeon.frontend.controller;

import com.huyeon.frontend.aop.refreshAccessTokenAop;
import com.huyeon.frontend.util.Fetch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/workspace/{groupUrl}")
public class CategoryController {

    private final Fetch fetch;

    @GetMapping("/new-category")
    public String createCategoryPage(
            @CookieValue(refreshAccessTokenAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            @PathVariable String groupUrl,
            Model model) {
        Map<String, Object> response = fetch.get("/workspace/" + groupUrl + "/new-category", newAccessToken);
        if (fetch.hasNoPermission(response)) return "pages/AccessDenied";
        model.addAllAttributes(response);
        return "pages/category/newcategory";
    }

    @GetMapping("/category")
    public String manageCategoryPage(
            @CookieValue(refreshAccessTokenAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            @PathVariable String groupUrl,
            Model model) {
        Map<String, Object> response = fetch.get("/workspace/" + groupUrl + "/category", newAccessToken);
        if (fetch.hasNoPermission(response)) return "pages/AccessDenied";
        model.addAllAttributes(response);
        return "pages/category/categorymanage";
    }


    @GetMapping("/{categoryName}")
    public String categoryPage(
            @CookieValue(refreshAccessTokenAop.REFRESH_KEY) String refreshToken,
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
