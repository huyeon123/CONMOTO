package com.huyeon.apiserver.controller.api;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.GroupDto;
import com.huyeon.apiserver.model.dto.MemberDto;
import com.huyeon.apiserver.model.dto.ResMessage;
import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@RestController
@Transactional
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupApiController {
    private final GroupService groupService;
    private final UserGroupService userGroupService;
    private final GroupManagerService managerService;
    private final MemberService memberService;
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> createGroup(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody GroupDto request) {
        Groups group = groupService.createGroup(userDetails.getUser(), request);
        userGroupService.registerUserToGroup(userDetails.getUser(), group);
        managerService.registerUserAsManager(userDetails.getUser(), group);
        categoryService.createRootCategory(group);

        return resMessageOfCreateGroup(group);
    }

    private ResponseEntity<?> resMessageOfCreateGroup(Groups group) {
        GroupDto resData = GroupDto.builder()
                .name(group.getName())
                .url(group.getUrlPath())
                .build();

        ResMessage resMessage = ResMessage.builder()
                .message("새 그룹이 생성되었습니다.")
                .data(resData)
                .success(true)
                .build();

        return new ResponseEntity<>(resMessage, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> editGroup(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String groupUrl,
            @RequestBody GroupDto request) {
        boolean success = groupService.editGroup(groupUrl, request);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping
    public ResponseEntity<?> deleteGroup(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String groupUrl) {
        User user = userDetails.getUser();
        String resMessage = groupService.deleteGroup(user, groupUrl);
        return new ResponseEntity<>(resMessage, HttpStatus.OK);
    }

    @PostMapping("/member")
    public ResponseEntity<?> saveMemberAuthority(
            @RequestParam String groupUrl,
            @RequestBody List<MemberDto> request) {
        //request를 시도 -> 성공/실패 사후처리
        Groups group = groupService.getGroupByUrl(groupUrl);
        boolean success = memberService.saveMemberRole(group, request);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @DeleteMapping("/member")
    public ResponseEntity<?> expelMember(
            @RequestParam String groupUrl,
            @RequestBody MemberDto request) {
        Groups group = groupService.getGroupByUrl(groupUrl);
        boolean success = userGroupService.expelUserFromGroup(request, group);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @PostMapping("/invite")
    public ResponseEntity<?> inviteMember(
            @RequestParam String groupUrl,
            @RequestBody String userEmail) {
        Groups group = groupService.getGroupByUrl(groupUrl);
        groupService.inviteMember(group, userEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
