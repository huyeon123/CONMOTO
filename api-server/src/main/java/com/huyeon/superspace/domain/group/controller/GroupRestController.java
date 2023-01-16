package com.huyeon.superspace.domain.group.controller;

import com.huyeon.superspace.domain.group.dto.CreateDto;
import com.huyeon.superspace.domain.group.dto.JoinDto;
import com.huyeon.superspace.domain.group.dto.MemberDto;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.service.NewGroupService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupRestController {
    private final NewGroupService groupService;

    @GetMapping("/{url}")
    public GroupDto getGroup(@PathVariable String url) {
        return groupService.findGroupByUrl(url);
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
            @RequestBody GroupDto request
    ) {
        return groupService.editGroup(url, request);
    }

    @DeleteMapping("/{url}")
    public void deleteGroup(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String url
    ) {
        groupService.deleteGroup(userEmail, url);
    }

    @PostMapping("/{url}/member-role")
    public void saveMemberRole(
            @PathVariable String url,
            @RequestBody List<MemberDto> request
    ) {
        groupService.saveMemberRole(url, request);
    }

    @DeleteMapping("/{url}/member")
    public void expelMember(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String url, @RequestBody Email request
    ) {
        groupService.expelMember(userEmail, url, request.getEmail());
    }

    @PostMapping("/{url}/invite")
    public void inviteMember(
            @PathVariable String url,
            @RequestBody Email request
    ) {
        groupService.inviteMember(url, request.getEmail());
    }

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public void joinMember(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody JoinDto request
    ) {
        groupService.joinMember(userEmail, request);
    }


    @Getter
    @Setter
    private static class Email {
        private String email;
    }
}
