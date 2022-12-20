package com.huyeon.superspace.web.domain.group;

import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.dto.MemberDto;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.group.service.GroupService;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.web.annotation.GroupPage;
import com.huyeon.superspace.web.annotation.NotGroupPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/workspace")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/find")
    public String getGroupPage(@RequestHeader("X-Authorization-Id") String userEmail) {
        if (hasAnyGroupPage(userEmail)) {
            return redirectFirstGroup(userEmail);
        }
        return "/workspace";
    }

    private boolean hasAnyGroupPage(String userEmail) {
        return !groupService.getGroups(userEmail).isEmpty();
    }

    private String redirectFirstGroup(String userEmail) {
        return "/workspace/" + firstGroupUrl(userEmail);
    }

    private String firstGroupUrl(String userEmail) {
        return groupService.getGroups(userEmail).get(0).getUrlPath();
    }

    @NotGroupPage
    @GetMapping
    public Map<String, Object> workSpacePage(@RequestHeader("X-Authorization-Id") String userEmail) {
        return new HashMap<>();
    }

    @GroupPage
    @GetMapping("/{groupUrl}")
    public Map<String, Object> workSpacePage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl) {
        Map<String, Object> response = new HashMap<>();

        String groupName = groupService.getGroupNameByUrl(groupUrl);

        response.put("title", groupName);

        return response;
    }

    @NotGroupPage
    @GetMapping("/new")
    public Map<String, Object> createGroupPage(@RequestHeader("X-Authorization-Id") String userEmail) {
        return new HashMap<>();
    }

    @GroupPage
    @GetMapping("/{groupUrl}/manage")
    public Map<String, Object> groupManagingPage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl) {
        Map<String, Object> response = new HashMap<>();

        WorkGroup group = groupService.getGroupByUrl(groupUrl);

        response.put("groupInfo", new GroupDto(group));

        return response;
    }

    @GroupPage
    @GetMapping("/{groupUrl}/members")
    public Map<String, Object> memberManagingPage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl) {
        Map<String, Object> response = new HashMap<>();

        WorkGroup group = groupService.getGroupByUrl(groupUrl);
        response.put("groupName", group.getName());

        List<MemberDto> members = getMembers(group);
        response.put("members", members);

        response.put("availableAuth", getAvailableAuthority());
        return response;
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

    @GroupPage
    @GetMapping("/{groupUrl}/delete")
    public Map<String, Object> groupDeletePage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl) {
        Map<String, Object> response = new HashMap<>();

        String groupName = groupService.getGroupNameByUrl(groupUrl);

        response.put("groupName", groupName);

        return response;
    }
}
