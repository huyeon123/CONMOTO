package com.huyeon.superspace.domain.group.controller;

import com.huyeon.superspace.domain.group.dto.*;
import com.huyeon.superspace.domain.group.service.NewGroupService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupRestController {
    private final NewGroupService groupService;

    @GetMapping("/{url}")
    public GroupDto getGroup(@PathVariable String url) {
        return groupService.getGroupByUrl(url);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createGroup(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody CreateDto request
    ) {
        return groupService.createGroup(userEmail, request);
    }

    @PutMapping("/{url}")
    public String editGroup(
            @PathVariable String url,
            @RequestParam String type,
            @RequestBody GroupDto request
    ) {
        return groupService.editGroup(url, type, request);
    }

    @DeleteMapping("/{url}")
    public void deleteGroup(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String url
    ) {
        groupService.deleteGroup(userEmail, url);
    }

    @DeleteMapping("/{url}/member")
    public void expelMember(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String url, @RequestBody ExpelDto request
    ) {
        groupService.expelMember(userEmail, url, request);
    }

    @PostMapping("/{url}/invite")
    public void inviteMember(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String url,
            @RequestBody Email request
    ) {
        groupService.inviteMember(url, userEmail, request.getEmail());
    }

    @PostMapping("/join")
    public boolean joinMember(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody JoinDto request
    ) {
        request.setUserEmail(userEmail);
        return groupService.joinMember(userEmail, request);
    }

    @GetMapping("/join-request-list")
    public JoinRequestDto getJoinRequestList(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestParam String groupUrl
    ) {
        //TODO: Permission Check
        return groupService.getJoinRequestList(groupUrl);
    }

    @PostMapping("/accept-join")
    public void acceptJoinRequest(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody JoinRequestDto request
    ) {
        groupService.acceptMember(request);
    }

    @DeleteMapping("/{url}/resign")
    @ResponseStatus(HttpStatus.OK)
    public void resignGroup(
            @PathVariable String url,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        groupService.resignGroup(url, userEmail);
    }

    @GetMapping("/grade")
    public GradeDto getGroupGrade(@RequestParam String url) {
        return groupService.getGroupGrade(url);
    }

    @PostMapping("/grade")
    public void saveGradeInfo(
            @RequestBody GradeDto request,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        groupService.saveGradeInfo(request);
    }

    @PutMapping("/member/grade")
    public void adjustMemberGrade(
            @RequestParam String memberId,
            @RequestParam int level
    ) {
        //TODO: Permission Check
        groupService.adjustMemberGrade(memberId, level);
    }

    @Getter
    @Setter
    private static class Email {
        private String email;
    }
}
