package com.huyeon.apiserver.controller.workspace;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.GroupDto;
import com.huyeon.apiserver.model.dto.MemberDto;
import com.huyeon.apiserver.model.entity.Groups;
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
        Groups group = groupService.getGroupByUrl(groupUrl);
        addGroupInfo(group, model);
        return "pages/group/groupmanage";
    }

    private void addGroupInfo(Groups group, Model model) {
        GroupDto groupDto = GroupDto.builder()
                .name(group.getName())
                .url(group.getUrlPath())
                .description(group.getDescription())
                .build();
        model.addAttribute("groupInfo", groupDto);
    }

    @GetMapping("/{groupUrl}/members")
    public String memberManagingPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String groupUrl, Model model) {
        Groups group = groupService.getGroupByUrl(groupUrl);
        addMemberInfo(group, model);
        addAvailableAuthority(model);
        return "pages/group/membermanage";
    }

    private void addMemberInfo(Groups group, Model model) {
        List<User> users = groupService.getUsers(group);
        List<MemberDto> members = users.stream()
                .map(user -> convertToMemberDto(group, user))
                .collect(Collectors.toList());
        model.addAttribute("members", members);
    }

    private MemberDto convertToMemberDto(Groups group, User user) {
        return MemberDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .groupRole(getGroupRole(group, user))
                .build();
    }

    private String getGroupRole(Groups group, User user) {
        return groupService.checkRole(group, user);
    }

    private void addAvailableAuthority(Model model) {
        List<String> authorities = List.of("일반 멤버", "관리자");
        model.addAttribute("availableAuth", authorities);
    }

    @GetMapping("/{groupUrl}/delete")
    public String groupDeletePage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String groupUrl, Model model) {

        return "pages/group/deletegroup";
    }
}
