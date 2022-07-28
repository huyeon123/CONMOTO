package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.service.UserService;
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
        boolean success = userService.editInfo(userDetails.getUsername(), editUser);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    //회원탈퇴
    @DeleteMapping("/resign")
    public ResponseEntity<?> resign(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean success = userService.resign(userDetails.getUsername());
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    //회원탈퇴 복구
    @PutMapping("/resign")
    public ResponseEntity<?> cancelResign(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean success = userService.cancelResign(userDetails.getUsername());
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

}
