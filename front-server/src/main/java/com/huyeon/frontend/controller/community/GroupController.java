package com.huyeon.frontend.controller.community;

import com.huyeon.frontend.aop.RequiredLogin;
import com.huyeon.frontend.exception.ForbiddenException;
import com.huyeon.frontend.util.Fetch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class GroupController {

    private final Fetch fetch;

    @GetMapping
    public String workSpacePage(
            HttpServletRequest request,
            String newAccessToken,
            Model model) {
        fetch.bindResponse("/community", newAccessToken, model);
        return "pages/group/community";
    }

    @RequiredLogin
    @GetMapping("/noty")
    public String notyPage(
            HttpServletRequest request,
            String newAccessToken,
            Model model
    ) {
        fetch.bindResponse("/community/noty", newAccessToken, model);
        return "pages/user/notypage";
    }

    @GetMapping("/{groupUrl}")
    public String workSpacePage(
            HttpServletRequest request,
            String newAccessToken,
            @PathVariable String groupUrl, Model model) {
        fetch.bindResponse("/community/" + groupUrl, newAccessToken, model);
        return "pages/group/groupmain";
    }

    @RequiredLogin
    @GetMapping("/new")
    public String createGroupPage(
            HttpServletRequest request,
            String newAccessToken,
            Model model) {
        fetch.bindResponse("/community/new", newAccessToken, model);
        return "pages/group/creategroup";
    }

    @RequiredLogin
    @GetMapping("/{groupUrl}/apply/join")
    public String joinGroupPage(
            HttpServletRequest request,
            String newAccessToken,
            @PathVariable String groupUrl, Model model) {
        fetch.bindResponse("/community/" + groupUrl, newAccessToken, model);
        return "pages/group/joingroup";
    }

    @RequiredLogin
    @GetMapping("/{groupUrl}/manage")
    public String groupManagingPage(
            HttpServletRequest request,
            String newAccessToken,
            @PathVariable String groupUrl, Model model) {
        Map<String, Object> response = fetch.get("/community/" + groupUrl + "/manage", newAccessToken);
        if (fetch.hasNoPermission(response)) throw new ForbiddenException();
        model.addAllAttributes(response);
        return "pages/group/groupmanage";
    }

    @RequiredLogin
    @GetMapping("/{groupUrl}/manage/members")
    public String memberManagingPage(
            HttpServletRequest request,
            String newAccessToken,
            @PathVariable String groupUrl, Model model) {
        Map<String, Object> response = fetch.get("/community/" + groupUrl + "/members", newAccessToken);
        if (fetch.hasNoPermission(response)) throw new ForbiddenException();
        model.addAllAttributes(response);
        return "pages/group/membermanage";
    }

    @RequiredLogin
    @GetMapping("/{groupUrl}/manage/members/{memberId}")
    public String memberManagingPage(
            HttpServletRequest request,
            String newAccessToken,
            @PathVariable String groupUrl,
            @PathVariable String memberId, Model model) {
        Map<String, Object> response = fetch.get("/community/" + groupUrl + "/members/" + memberId, newAccessToken);
        if (fetch.hasNoPermission(response)) throw new ForbiddenException();
        model.addAllAttributes(response);
        return "pages/group/memberpage";
    }

    @RequiredLogin
    @GetMapping("/{groupUrl}/manage/category")
    public String manageCategoryPage(
            HttpServletRequest request,
            String newAccessToken,
            @PathVariable String groupUrl,
            Model model) {
        Map<String, Object> response = fetch.get("/community/" + groupUrl + "/manage/category", newAccessToken);
        if (fetch.hasNoPermission(response)) throw new ForbiddenException();
        model.addAllAttributes(response);
        return "pages/group/categorymanage";
    }

    @RequiredLogin
    @GetMapping("/{groupUrl}/manage/join")
    public String joinManagingPage(
            HttpServletRequest request,
            String newAccessToken,
            @PathVariable String groupUrl, Model model) {
        Map<String, Object> response = fetch.get("/community/" + groupUrl + "/members", newAccessToken);
        if (fetch.hasNoPermission(response)) throw new ForbiddenException();
        model.addAllAttributes(response);
        return "pages/group/joinmanage";
    }

    @RequiredLogin
    @GetMapping("/{groupUrl}/manage/grade")
    public String gradeManagingPage(
            HttpServletRequest request,
            String newAccessToken,
            @PathVariable String groupUrl, Model model) {
        Map<String, Object> response = fetch.get("/community/" + groupUrl + "/manage", newAccessToken);
        if (fetch.hasNoPermission(response)) throw new ForbiddenException();
        model.addAllAttributes(response);
        return "pages/group/grademanage";
    }
}

