package com.huyeon.apiserver.controller.workspace;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkSpaceController {

    private final GroupService groupService;

    @GetMapping
    public String workSpacePage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model) {
        if (isPossibleRedirect(userDetails)) {
            return redirectFirstGroup(userDetails);
        }
        return "pages/workspace";
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

    @GetMapping("/{groupUrl}")
    public String workSpacePage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String groupUrl,
            Model model) {
        String groupName = groupService.getGroupNameByUrl(groupUrl);
        model.addAttribute("title", groupName);
        return "pages/workspace";
    }


}
