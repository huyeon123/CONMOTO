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
@RequestMapping("/workspace")
public class GroupController {

    private final Fetch fetch;

    @GetMapping
    public String workSpacePage(
            @CookieValue(refreshAccessTokenAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            Model model) {
        fetch.bindResponse("/workspace", newAccessToken, model);
        return "pages/workspace";
    }

    @GetMapping("/{groupUrl}")
    public String workSpacePage(
            @CookieValue(refreshAccessTokenAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            @PathVariable String groupUrl, Model model) {
        fetch.bindResponse("/workspace/" + groupUrl, newAccessToken, model);
        return "pages/group/groupmain";
    }

    @GetMapping("/new")
    public String createGroupPage(
            @CookieValue(refreshAccessTokenAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            Model model) {
        fetch.bindResponse("/workspace/new", newAccessToken, model);
        return "pages/group/creategroup";
    }

    @GetMapping("/{groupUrl}/manage")
    public String groupManagingPage(
            @CookieValue(value = refreshAccessTokenAop.REFRESH_KEY, required = false) String refreshToken,
            String newAccessToken,
            @PathVariable String groupUrl, Model model) {
        Map<String, Object> response = fetch.get("/workspace/" + groupUrl + "/manage", newAccessToken);
        if (fetch.hasNoPermission(response)) return "pages/AccessDenied";
        model.addAllAttributes(response);
        return "pages/group/groupmanage";
    }

    @GetMapping("/{groupUrl}/members")
    public String memberManagingPage(
            @CookieValue(refreshAccessTokenAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            @PathVariable String groupUrl, Model model) {
        Map<String, Object> response = fetch.get("/workspace/" + groupUrl + "/members", newAccessToken);
        if (fetch.hasNoPermission(response)) return "pages/AccessDenied";
        model.addAllAttributes(response);
        return "pages/group/membermanage";
    }

    @GetMapping("/{groupUrl}/delete")
    public String groupDeletePage(
            @CookieValue(refreshAccessTokenAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            @PathVariable String groupUrl, Model model) {
        Map<String, Object> response = fetch.get("/workspace/" + groupUrl + "/delete", newAccessToken);
        if (fetch.hasNoPermission(response)) return "pages/AccessDenied";
        model.addAllAttributes(response);
        return "pages/group/deletegroup";
    }
}

