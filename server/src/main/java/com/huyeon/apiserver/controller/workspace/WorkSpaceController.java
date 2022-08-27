package com.huyeon.apiserver.controller.workspace;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.BoardResDto;
import com.huyeon.apiserver.model.dto.HeaderDto;
import com.huyeon.apiserver.model.dto.SideBarDto;
import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.service.BoardService;
import com.huyeon.apiserver.service.ContentBlockService;
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

    private final GroupService groupService;
    private final BoardService boardService;
    private final ContentBlockService blockService;

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

        //TODO: 보드당 요약 컨텐츠 가져오기

        return "pages/workspace";
    }


}
