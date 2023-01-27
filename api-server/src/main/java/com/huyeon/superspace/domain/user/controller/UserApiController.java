package com.huyeon.superspace.domain.user.controller;

import com.huyeon.superspace.domain.user.dto.UserUpdateDto;
import com.huyeon.superspace.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody UserUpdateDto editUser) {
        userService.editInfo(userEmail, editUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //회원탈퇴
    @DeleteMapping("/resign")
    public ResponseEntity<?> resign(@RequestHeader("X-Authorization-Id") String userEmail) {
        userService.resign(userEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //회원탈퇴 복구
    @PutMapping("/resign")
    public ResponseEntity<?> cancelResign(@RequestHeader("X-Authorization-Id") String userEmail) {
        userService.cancelResign(userEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
