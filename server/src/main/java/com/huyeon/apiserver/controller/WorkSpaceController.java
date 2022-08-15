package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.HeaderDto;
import com.huyeon.apiserver.model.dto.SideBarDto;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.service.GroupService;
import com.huyeon.apiserver.service.WorkSpaceService;
import lombok.NonNull;
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
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkSpaceController {

    private final WorkSpaceService workSpaceService;
    private final GroupService groupService;

    @GetMapping
    public String workSpacePage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model) {
        if (isPossibleRedirect(userDetails)) {
            return redirectFirstGroup(userDetails);
        }

        addDefaultSideBarAndHeader(userDetails, model);
        return "workspace";
    }

    @GetMapping("/{groupUrl}")
    public String workSpacePage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String groupUrl,
            Model model) {
        addGroupSideBarAndHeader(userDetails, groupUrl, model);
        return "workspace";
    }

    private boolean isPossibleRedirect(UserDetailsImpl userDetails) {
        return !groupService.getGroups(userDetails.getUser()).isEmpty();
    }

    private String redirectFirstGroup(UserDetailsImpl userDetails) {
        return "redirect:/workspace/" + firstGroupUrl(userDetails);
    }

    private String firstGroupUrl(UserDetailsImpl userDetails) {
        return groupService.getGroups(userDetails.getUser()).get(0).getUrlPath();
    }

    private void addDefaultSideBarAndHeader(UserDetailsImpl userDetails, Model model) {
        User user = userDetails.getUser();
        SideBarDto sideBar = blankSideBar(user);
        HeaderDto appHeader = blankHeader(user.getName());
        addAttributes(model, sideBar, appHeader);
    }

    private SideBarDto blankSideBar(User user) {
        return SideBarDto.builder()
                .groups(workSpaceService.getGroups(user))
                .categories(List.of())
                .build();
    }

    private HeaderDto blankHeader(@NonNull String name) {
        return HeaderDto.builder()
                .userName(name)
                .groupName("그룹을 선택하세요!")
                .build();
    }

    private void addGroupSideBarAndHeader(UserDetailsImpl userDetails, String groupUrl, Model model) {
        SideBarDto sideBar = workSpaceService.getSideBar(userDetails, groupUrl);
        HeaderDto appHeader = workSpaceService.getAppHeader(userDetails, groupUrl);
        addAttributes(model, sideBar, appHeader);
    }

    private void addAttributes(Model model, SideBarDto sideBar, HeaderDto header) {
        model.addAttribute("sideBar", sideBar);
        model.addAttribute("appHeader", header);
    }
}
