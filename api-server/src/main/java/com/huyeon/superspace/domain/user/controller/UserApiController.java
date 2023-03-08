package com.huyeon.superspace.domain.user.controller;

import com.huyeon.superspace.domain.user.dto.EditRes;
import com.huyeon.superspace.domain.user.dto.UserUpdateDto;
import com.huyeon.superspace.domain.user.service.UserService;
import com.huyeon.superspace.web.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {
    private final UserService userService;

    @GetMapping
    public UserDto getUser(@RequestHeader("X-Authorization-Id") String userEmail) {
        return userService.getUser(userEmail);
    }

    //회원정보 수정
    @PutMapping("/edit/name")
    public EditRes editName(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody UserUpdateDto editUser) {
        return userService.editName(userEmail, editUser);
    }

    @PutMapping("/edit/birthday")
    public void editBirthday(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody UserUpdateDto editUser) {
        userService.editBirthday(userEmail, editUser);
    }

    @PutMapping("/edit/password")
    public EditRes editPassword(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody UserUpdateDto editUser) {
        return userService.editPassword(userEmail, editUser);
    }

    //회원탈퇴
    @DeleteMapping("/resign")
    public void resign(@RequestHeader("X-Authorization-Id") String userEmail) {
        userService.resign(userEmail);
    }

    //회원탈퇴 복구
    @PutMapping("/resign")
    public void cancelResign(@RequestHeader("X-Authorization-Id") String userEmail) {
        userService.cancelResign(userEmail);
    }

}
