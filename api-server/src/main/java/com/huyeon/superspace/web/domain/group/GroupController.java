package com.huyeon.superspace.web.domain.group;

import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.dto.MemberDto;
import com.huyeon.superspace.domain.group.service.NewGroupService;
import com.huyeon.superspace.web.annotation.GroupPage;
import com.huyeon.superspace.web.annotation.ManagerPage;
import com.huyeon.superspace.web.annotation.NotGroupPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class GroupController {

    private final NewGroupService groupService;

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

        String groupName = groupService.findGroupByUrl(groupUrl).getName();
        response.put("title", groupName);

        return response;
    }

    @NotGroupPage
    @GetMapping("/new")
    public Map<String, Object> createGroupPage(@RequestHeader("X-Authorization-Id") String userEmail) {
        return new HashMap<>();
    }

    @GroupPage
    @ManagerPage
    @GetMapping("/{groupUrl}/manage")
    public Map<String, Object> groupManagingPage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl) {
        Map<String, Object> response = new HashMap<>();

        GroupDto group = groupService.findGroupByUrl(groupUrl);
        response.put("groupInfo", group);

        return response;
    }

    @GroupPage
    @ManagerPage
    @GetMapping("/{groupUrl}/members")
    public Map<String, Object> memberManagingPage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl) {
        Map<String, Object> response = new HashMap<>();

        GroupDto group = groupService.findGroupByUrl(groupUrl);
        response.put("groupName", group.getName());

        List<MemberDto> members = getMembers(group);
        response.put("members", members);

        response.put("availableAuth", getAvailableAuthority());
        return response;
    }

    private List<MemberDto> getMembers(GroupDto group) {
        return groupService.findMembersById(group.getId());
    }

    private List<String> getAvailableAuthority() {
        return List.of("일반 멤버", "그룹 관리자");
    }

    @GroupPage
    @ManagerPage
    @GetMapping("/{groupUrl}/delete")
    public Map<String, Object> groupDeletePage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl) {
        Map<String, Object> response = new HashMap<>();

        String groupName = groupService.findGroupByUrl(groupUrl).getName();

        response.put("groupName", groupName);
        response.put("status", "success");

        return response;
    }
}
