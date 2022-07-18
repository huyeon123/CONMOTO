package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.UserSignInReq;
import com.huyeon.apiserver.model.dto.UserSignUpReq;
import com.huyeon.apiserver.model.dto.ResMessage;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.huyeon.apiserver.support.JsonParse.*;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원정보
    @GetMapping("/profile")
    public String userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        if(user == null) return "회원 정보가 존재하지 않습니다.";
        return writeJson(user);
    }

    //회원정보 수정
    @PostMapping("/edit")
    public String editInfo(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody String editForm) {
        if(userService.editInfo(userDetails.getUsername(), editForm)) {
            return "정보가 정상적으로 반영되었습니다.";
        }
        return "회원정보 수정에 실패했습니다.";
    }

    //회원탈퇴
    @DeleteMapping
    public String resign(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(userService.resign(userDetails.getUsername())) return "이용해주셔서 감사합니다.";
        return "회원 탈퇴에 실패했습니다";
    }
}
