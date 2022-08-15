package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.model.dto.MemberDto;
import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.service.GroupService;
import com.huyeon.apiserver.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final GroupService groupService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<?> saveMemberAuthority(
            @RequestParam String groupUrl,
            @RequestBody List<MemberDto> request) {
        //request를 시도 -> 성공/실패 사후처리
        Groups group = groupService.getGroupByUrl(groupUrl);
        boolean success = memberService.saveMemberRole(group, request);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }
}
