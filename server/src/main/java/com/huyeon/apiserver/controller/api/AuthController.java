package com.huyeon.apiserver.controller.api;

import com.huyeon.apiserver.model.dto.ResMessage;
import com.huyeon.apiserver.model.dto.UserSignInReq;
import com.huyeon.apiserver.model.dto.UserSignUpReq;
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

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResMessage> signUp(@RequestBody UserSignUpReq request) {
        ResMessage response = new ResMessage();
        if (authService.signUp(request)) {
            log.info("회원 가입 완료");
            response.setMessage("회원 가입에 성공했습니다.");
            response.setSuccess(true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.setMessage("회원 가입에 실패했습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> logIn(@RequestBody UserSignInReq request) {
        try {
            authService.logIn(request);
            log.info(request.getEmail() + "로그인");
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            log.info(request.getEmail() + "로그인 실패");
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
    }

    @PostMapping("/check")
    public ResponseEntity<ResMessage> checkDuplicateEmail(@RequestBody String email) {
        ResMessage response = new ResMessage();
        if (authService.checkDuplicateEmail(email)) {
            response.setMessage("중복된 이메일입니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.setMessage("사용 가능한 이메일입니다.");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
