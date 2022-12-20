package com.huyeon.frontend.controller;

import com.huyeon.frontend.aop.RequestAop;
import com.huyeon.frontend.util.Fetch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/workspace")
public class GroupController {

    private final Fetch fetch;

    @GetMapping
    public String workSpacePage(
            @CookieValue(RequestAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            Model model) {
        fetch.bindResponse("/workspace", newAccessToken, model);
        return "pages/workspace";
    }

    @GetMapping("/{groupUrl}")
    public String workSpacePage(
            @CookieValue(RequestAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            @PathVariable String groupUrl, Model model) {
        fetch.bindResponse("/workspace/" + groupUrl, newAccessToken, model);
        return "pages/group/groupmain";
    }

    @GetMapping("/new")
    public String createGroupPage(
            @CookieValue(RequestAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            Model model) {
        fetch.bindResponse("/workspace/new", newAccessToken, model);
        return "pages/group/creategroup";
    }

    @GetMapping("/{groupUrl}/manage")
    public String groupManagingPage(
            @CookieValue(RequestAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            @PathVariable String groupUrl, Model model) {
        fetch.bindResponse("/workspace/" + groupUrl + "/manage", newAccessToken, model);
        return "pages/group/groupmanage";
    }

    @GetMapping("/{groupUrl}/members")
    public String memberManagingPage(
            @CookieValue(RequestAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            @PathVariable String groupUrl, Model model) {
        fetch.bindResponse("/workspace/" + groupUrl + "/members", newAccessToken, model);
        return "pages/group/membermanage";
    }

    @GetMapping("/{groupUrl}/delete")
    public String groupDeletePage(
            @CookieValue(RequestAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            @PathVariable String groupUrl, Model model) {
        fetch.bindResponse("/workspace/" + groupUrl + "/delete", newAccessToken, model);
        return "pages/group/deletegroup";
    }
}

