package com.huyeon.authserver.auth.controller;

import com.huyeon.authserver.auth.dto.UserSignInReq;
import com.huyeon.authserver.auth.dto.UserSignUpReq;
import com.huyeon.authserver.auth.service.AuthService;
import com.huyeon.authserver.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody UserSignUpReq request) {
        authService.signUp(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> logIn(@RequestBody UserSignInReq request) {
        String jwt = "Bearer " + authService.logIn(request);

        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkDuplicateEmail(@RequestBody String email) {
        boolean isDuplicate = authService.isDuplicateEmail(email);
        return new ResponseEntity<>(isDuplicate, HttpStatus.OK);
    }
}
