package com.huyeon.authserver.auth.controller;

import com.huyeon.authserver.auth.dto.UserSignInReq;
import com.huyeon.authserver.auth.dto.UserSignUpReq;
import com.huyeon.authserver.auth.dto.UserTokenInfo;
import com.huyeon.authserver.auth.service.AuthService;
import com.huyeon.authserver.jwt.JwtFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final int ACCESS_TOKEN_EXPIRE_TIME = 30 * 60;
    private static final int REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60;
    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody UserSignUpReq request) {
        authService.signUp(request);
    }

    @PostMapping("/login")
    public String logIn(@RequestBody UserSignInReq request, HttpServletResponse response) {
        UserTokenInfo userTokenInfo = authService.logIn(request);

        //TODO: Refresh Token Redis 저장 - Service단에서
        Cookie access = setCookie("Super-Space-Access", userTokenInfo.getAccessToken(), ACCESS_TOKEN_EXPIRE_TIME);
        Cookie refresh = setCookie("Super-Space-Refresh", userTokenInfo.getRefreshToken(), REFRESH_TOKEN_EXPIRE_TIME);
        response.addCookie(access);
        response.addCookie(refresh);

        return userTokenInfo.getAccessToken();
    }

    private Cookie setCookie(String name, String token, int expireTime) {
        Cookie cookie = new Cookie(name, token);
        cookie.setPath("/");
        cookie.setMaxAge(expireTime);
        cookie.setHttpOnly(true);

        return cookie;
    }

    @PostMapping("/check")
    public boolean checkDuplicateEmail(@RequestBody SingleBody email) {
        return authService.isDuplicateEmail(email.getBody());
    }

    @GetMapping("/refresh")
    public String generateNewAccessToken(HttpServletRequest request) {
        String refreshToken = findRefreshToken(request);
        return authService.generateNewAccessToken(refreshToken);
    }

    private String findRefreshToken(HttpServletRequest request) {
        String header = request.getHeader("Super-Space-Refresh");

        if (header == null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("Super-Space-Refresh")) {
                    return cookie.getValue();
                }
            }
        }

        return header;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class SingleBody{
        String body;
    }
}
