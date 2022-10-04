package com.huyeon.apiserver.controller.page;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.GroupDto;
import com.huyeon.apiserver.model.dto.MemberDto;
import com.huyeon.apiserver.model.entity.WorkGroup;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/new")
    public String createGroupPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model
    ) {
        return "pages/group/creategroup";
    }

    @GetMapping("/{groupUrl}/manage")
    public String groupManagingPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String groupUrl, Model model) {
        WorkGroup group = groupService.getGroupByUrl(groupUrl);
        model.addAttribute("groupInfo", new GroupDto(group));
        return "pages/group/groupmanage";
    }

    @GetMapping("/{groupUrl}/members")
    public String memberManagingPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String groupUrl, Model model) {
        WorkGroup group = groupService.getGroupByUrl(groupUrl);
        model.addAttribute("groupName", group.getName());

        List<MemberDto> members = getMembers(group);
        model.addAttribute("members", members);

        model.addAttribute("availableAuth", getAvailableAuthority());
        return "pages/group/membermanage";
    }

    private List<MemberDto> getMembers(WorkGroup group) {
        List<User> users = groupService.getUsers(group);
        return users.stream()
                .map(user -> new MemberDto(user, getGroupRole(group, user)))
                .collect(Collectors.toList());
    }

    private String getGroupRole(WorkGroup group, User user) {
        return groupService.checkRole(group, user);
    }

    private List<String> getAvailableAuthority() {
        return List.of("일반 멤버", "그룹 관리자");
    }

    @GetMapping("/{groupUrl}/delete")
    public String groupDeletePage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String groupUrl, Model model) {
        String groupName = groupService.getGroupNameByUrl(groupUrl);
        model.addAttribute("groupName", groupName);
        return "pages/group/deletegroup";
    }
}
