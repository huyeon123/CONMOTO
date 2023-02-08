package com.huyeon.authserver.auth.controller;

import com.huyeon.authserver.auth.dto.UserSignInReq;
import com.huyeon.authserver.auth.dto.UserSignUpReq;
import com.huyeon.authserver.auth.dto.UserTokenInfo;
import com.huyeon.authserver.auth.service.AuthService;
import com.huyeon.authserver.email.EmailService;
import com.huyeon.authserver.utils.CookieUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_KEY_NAME = "Super-Space-Refresh";
    private final AuthService authService;
    private final EmailService loginCodeEmailService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody UserSignUpReq request) {
        authService.signUp(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> logIn(@RequestBody UserSignInReq request, HttpServletResponse response) {
        try {
            UserTokenInfo userTokenInfo = authService.logIn(request);

            setCookie(response, userTokenInfo);

            return new ResponseEntity<>(userTokenInfo, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.ACCEPTED);
        } catch (RedisConnectionFailureException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private void setCookie(HttpServletResponse response, UserTokenInfo token) {
        CookieUtils.addCookie(
                response,
                REFRESH_KEY_NAME,
                token.getRefreshToken(),
                (int) (token.getRefreshTokenExpireTime() / 1000)
        );
    }

    @PostMapping("/login-code")
    public void generateTempLoginCode(@RequestBody Email request) {
        loginCodeEmailService.send(request.getEmail());
    }

    @PostMapping("/check")
    public boolean checkDuplicateEmail(@RequestBody Email request) {
        return authService.isDuplicateEmail(request.getEmail());
    }

    @GetMapping("/refresh")
    public String generateNewAccessToken(
            HttpServletRequest request,
            @CookieValue(name = REFRESH_KEY_NAME, required = false) String refreshToken) {
        if (refreshToken == null) refreshToken = getRefreshToken(request);
        return authService.generateNewAccessToken(refreshToken);
    }

    private String getRefreshToken(HttpServletRequest request) {
        return request.getHeader(REFRESH_KEY_NAME);
    }

    @GetMapping("/logout")
    public void logout(
            @CookieValue(REFRESH_KEY_NAME) String refreshToken,
            HttpServletResponse response) {
        authService.logout(refreshToken);
        response.addCookie(resetCookie());
    }

    private Cookie resetCookie() {
        Cookie cookie = new Cookie(REFRESH_KEY_NAME, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);

        return cookie;
    }

    @GetMapping("/oauth2/success")
    public UserTokenInfo loginSuccess(
            @RequestParam("accessToken") String accessToken,
            @RequestParam("accessTokenExpireTime") long accessTokenExpireTime,
            @RequestParam("refreshToken") String refreshToken,
            @RequestParam("refreshTokenExpireTime") long refreshTokenExpireTime,
            HttpServletResponse response
    ) {
        UserTokenInfo userTokenInfo
                = new UserTokenInfo(accessToken, accessTokenExpireTime, refreshToken, refreshTokenExpireTime);

        try {
            response.sendRedirect("http://localhost:8200/workspace");
        } catch (IOException e) {
            log.error("FE 서버로 리다이렉트 실패");
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        setCookie(response, userTokenInfo);
        return userTokenInfo;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class Email {
        String email;
    }
}
