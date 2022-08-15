package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.GroupDto;
import com.huyeon.apiserver.model.dto.ResMessage;
import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.service.CategoryService;
import com.huyeon.apiserver.service.GroupManagerService;
import com.huyeon.apiserver.service.GroupService;
import com.huyeon.apiserver.service.UserGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@Slf4j
@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupApiController {
    private final GroupService groupService;
    private final UserGroupService userGroupService;
    private final GroupManagerService managerService;
    private final CategoryService categoryService;

    @Transactional
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
}
