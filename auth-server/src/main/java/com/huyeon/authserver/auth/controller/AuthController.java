package com.huyeon.authserver.auth.controller;

import com.huyeon.authserver.auth.dto.TempLoginReqDTO;
import com.huyeon.authserver.auth.dto.UserSignInReq;
import com.huyeon.authserver.auth.dto.UserSignUpReq;
import com.huyeon.authserver.auth.dto.UserTokenInfo;
import com.huyeon.authserver.auth.exception.NonMembersException;
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
import java.util.concurrent.CompletableFuture;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_KEY_NAME = "CONMOTO_JWT";
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
            if (authService.isSocialAccount(request.getEmail())) {
                throw new BadCredentialsException("소셜 아이디입니다. 소셜 로그인으로 진행해주세요.");
            }

            UserTokenInfo userTokenInfo = authService.logIn(request);

            setCookie(response, userTokenInfo);

            return new ResponseEntity<>(userTokenInfo, HttpStatus.OK);
        } catch (NonMembersException | BadCredentialsException e) {
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
        CompletableFuture
                .runAsync(() -> loginCodeEmailService.send(request.getEmail()))
                .thenAcceptAsync((returnValue) -> log.info("[이메일 발송 완료] : 수신자 - {}", request.getEmail()));
    }

    @PostMapping("/login/temp")
    public UserTokenInfo logInTemporally(
            @RequestBody TempLoginReqDTO request,
            HttpServletResponse response
    ) {
        UserTokenInfo userTokenInfo = authService.logInTemporally(request);
        setCookie(response, userTokenInfo);
        return userTokenInfo;
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
        if (refreshToken == null) return getAnonymousToken();
        String newAccessToken = authService.generateNewAccessToken(refreshToken);
        log.debug("AccessToken 생성완료");
        return newAccessToken;
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

        setCookie(response, userTokenInfo);

        try {
            response.sendRedirect("https://conmoto.site/community");
        } catch (IOException e) {
            log.error("FE 서버로 리다이렉트 실패");
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return userTokenInfo;
    }

    @GetMapping("/anonymous")
    public String getAnonymousToken() {
        return authService.getAnonymousToken();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class Email {
        String email;
    }
}