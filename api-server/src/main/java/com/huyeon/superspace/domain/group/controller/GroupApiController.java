package com.huyeon.superspace.domain.group.controller;

import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.dto.MemberDto;
import com.huyeon.superspace.domain.group.service.GroupService;
import com.huyeon.superspace.domain.group.service.MemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupApiController {
    private final GroupService groupService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<?> createGroup(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody GroupDto request) {
        GroupDto group = groupService.createGroup(userEmail, request);
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @PutMapping("/{groupUrl}")
    public ResponseEntity<?> editGroup(
            @PathVariable String groupUrl,
            @RequestBody GroupDto request) {
        groupService.editGroup(groupUrl, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{groupUrl}")
    public ResponseEntity<?> deleteGroup(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl) {
        try {
            groupService.deleteGroup(userEmail, groupUrl);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{groupUrl}/member")
    public ResponseEntity<?> saveMemberAuthority(
            @PathVariable String groupUrl,
            @RequestBody List<MemberDto> request) {
        memberService.saveMemberRole(groupUrl, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{groupUrl}/member")
    public ResponseEntity<?> expelMember(
            @PathVariable String groupUrl,
            @RequestBody MemberDto request) {
        memberService.expelUser(groupUrl, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{groupUrl}/invite")
    public ResponseEntity<?> inviteMember(
            @PathVariable String groupUrl,
            @RequestBody Invite invite) {
        memberService.inviteMember(groupUrl, invite.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{groupUrl}/join")
    public ResponseEntity<?> joinMember(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl) {
        try {
            memberService.joinMember(userEmail, groupUrl);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @Getter
    @Setter
    private static class Invite {
        private String email;
    }

}
