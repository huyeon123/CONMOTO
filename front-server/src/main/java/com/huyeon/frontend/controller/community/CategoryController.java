package com.huyeon.frontend.controller.community;

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

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/community/{groupUrl}")
public class CategoryController {

    private final Fetch fetch;

    @GetMapping("/{categoryName}")
    public String categoryPage(
            HttpServletRequest request,
            String newAccessToken,
            @PathVariable String groupUrl,
            @PathVariable String categoryName,
            Model model) {
        fetch.bindResponse(
                "/community/" + groupUrl + "/" + categoryName,
                newAccessToken, model
        );
        return "pages/category/category";
    }
}
