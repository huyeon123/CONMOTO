package com.huyeon.superspace.domain.user.controller;

import com.huyeon.superspace.global.model.UserDetailsImpl;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {
    private final UserService userService;

    //회원정보 수정
    @PutMapping("/edit")
    public ResponseEntity<?> editInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody User editUser) {
        userService.editInfo(userDetails.getUsername(), editUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //회원탈퇴
    @DeleteMapping("/resign")
    public ResponseEntity<?> resign(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.resign(userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //회원탈퇴 복구
    @PutMapping("/resign")
    public ResponseEntity<?> cancelResign(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.cancelResign(userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
