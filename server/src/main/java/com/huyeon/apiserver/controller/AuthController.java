package com.huyeon.apiserver.controller;


import com.huyeon.apiserver.model.dto.ResMessage;
import com.huyeon.apiserver.model.dto.UserSignInReq;
import com.huyeon.apiserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResMessage> signIn(@RequestBody UserSignInReq request) {
        ResMessage response = new ResMessage();
        try {
            authService.signIn(request);
            log.info(request.getEmail() + "로그인");
            response.setMessage("로그인에 성공했습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.info(request.getEmail() + "로그인 실패");
        }
        response.setMessage("로그인에 실패했습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
