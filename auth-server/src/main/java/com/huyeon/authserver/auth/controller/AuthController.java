package com.huyeon.authserver.auth.controller;

import com.huyeon.authserver.auth.dto.UserSignInReq;
import com.huyeon.authserver.auth.dto.UserSignUpReq;
import com.huyeon.authserver.auth.dto.UserTokenInfo;
import com.huyeon.authserver.auth.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public String logIn(@RequestBody UserSignInReq request, HttpServletResponse response) {
        UserTokenInfo userTokenInfo = authService.logIn(request);

        Cookie refresh = setCookie(userTokenInfo);
        response.addCookie(refresh);

        return userTokenInfo.getAccessToken();
    }

    private Cookie setCookie(UserTokenInfo token) {
        Cookie cookie = new Cookie("Super-Space-Refresh", token.getRefreshToken());
        cookie.setPath("/");
        cookie.setMaxAge((int) (token.getRefreshTokenExpireTime() / 1000));
        cookie.setHttpOnly(true);

        return cookie;
    }

    @PostMapping("/check")
    public boolean checkDuplicateEmail(@RequestBody SingleBody email) {
        return authService.isDuplicateEmail(email.getBody());
    }

    @GetMapping("/refresh")
    public String generateNewAccessToken(@CookieValue(name = "Super-Space-Refresh") String refreshToken) {
        return authService.generateNewAccessToken(refreshToken);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class SingleBody{
        String body;
    }
}
