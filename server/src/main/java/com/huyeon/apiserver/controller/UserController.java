package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public String signUp(@RequestBody String signUpForm) {
        if(userService.signUp(signUpForm)){
            log.info("회원 가입 완료");
            return "환영합니다.";
        }
        return "회원 가입에 실패했습니다.";
    }

    //회원정보
    @GetMapping("/{id}/profile")
    public String userInfo(@PathVariable Long id) {
        String userInfo = userService.userInfo(id);
        if(userInfo == null) return "회원 정보가 존재하지 않습니다.";
        return userInfo;
    }

    //회원정보 수정
    @PostMapping("/{id}/edit")
    public String editInfo(@PathVariable Long id, @RequestBody String editForm) {
        if(userService.editInfo(id, editForm)) {
            return "정보가 정상적으로 반영되었습니다.";
        }
        return "회원정보 수정에 실패했습니다.";
    }

    //회원탈퇴
    @DeleteMapping("/{id}")
    public String resign(@PathVariable Long id) {
        if(userService.resign(id)) return "이용해주셔서 감사합니다.";
        return "회원 탈퇴에 실패했습니다";
    }
}
