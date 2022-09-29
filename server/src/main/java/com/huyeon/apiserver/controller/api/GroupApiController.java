package com.huyeon.apiserver.controller.api;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.GroupDto;
import com.huyeon.apiserver.model.dto.MemberDto;
import com.huyeon.apiserver.service.GroupService;
import com.huyeon.apiserver.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody GroupDto request) {
        GroupDto group = groupService.createGroup(userDetails.getUser(), request);
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> editGroup(
            @RequestParam String groupUrl,
            @RequestBody GroupDto request) {
        groupService.editGroup(groupUrl, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteGroup(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String groupUrl) {
        try {
            groupService.deleteGroup(userDetails.getUser(), groupUrl);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/member")
    public ResponseEntity<?> saveMemberAuthority(
            @RequestParam String groupUrl,
            @RequestBody List<MemberDto> request) {
        memberService.saveMemberRole(groupUrl, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/member")
    public ResponseEntity<?> expelMember(
            @RequestParam String groupUrl,
            @RequestBody MemberDto request) {
        memberService.expelUser(groupUrl, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/invite")
    public ResponseEntity<?> inviteMember(
            @RequestParam String groupUrl,
            @RequestBody String userEmail) {
        groupService.inviteMember(groupUrl, userEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/join")
    public ResponseEntity<?> joinMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String groupUrl) {
        groupService.joinMember(userDetails.getUser(), groupUrl);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
