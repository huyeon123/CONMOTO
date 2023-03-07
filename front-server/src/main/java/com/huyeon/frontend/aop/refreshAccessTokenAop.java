package com.huyeon.frontend.aop;

import com.huyeon.frontend.jwt.TokenExtractor;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class refreshAccessTokenAop {
    @Value("${conmoto.server.auth}")
    private String AUTH_SERVER_ADDR;
    public static final String REFRESH_KEY = "CONMOTO_JWT";

    private final TokenExtractor tokenExtractor;

    @Pointcut("@annotation(RequiredLogin)")
    public void needSideBarAndHeader() {
    }

    @Pointcut("within(com.huyeon.frontend.controller.community.*) && !@annotation(RequiredLogin)")
    public void notRequiredLogin() {
    }

    @Around("notRequiredLogin()")
    private Object getAnonymousToken(ProceedingJoinPoint joinPoint) throws  Throwable {
        Object[] args = joinPoint.getArgs();
        String refreshToken = getRefreshToken(args[0]);

        if (refreshToken == null) args[1] = getAnonymousToken();
        else args[1] = generateNewAccessToken(refreshToken);

        return joinPoint.proceed(args);
    }

    private String getAnonymousToken() {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(
                AUTH_SERVER_ADDR + "/anonymous",
                HttpMethod.GET,
                null,
                String.class
        ).getBody();
    }

    @Around("needSideBarAndHeader()")
    private Object refreshAccessToken(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String refreshToken = getRefreshToken(args[0]);

        args[1] = generateNewAccessToken(refreshToken);

        if (args[1] == null) {
            return "redirect:https://conmoto.site/login";
        }

        return joinPoint.proceed(args);
    }

    private String getRefreshToken(Object arg) {
        HttpServletRequest request = (HttpServletRequest) arg;
        Cookie[] cookies = request.getCookies();

        if (Objects.isNull(cookies)) return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH_KEY)) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private String generateNewAccessToken(String refreshToken) {
        if (isNotValidToken(refreshToken)) return null;

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(
                AUTH_SERVER_ADDR + "/refresh",
                HttpMethod.GET,
                getRequestBody(refreshToken),
                String.class
        ).getBody();
    }

    private boolean isNotValidToken(String token) {
        return token == null || !tokenExtractor.validateToken(token);
    }

    private HttpEntity<?> getRequestBody(String refreshToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(REFRESH_KEY, refreshToken);

        return new HttpEntity<>(httpHeaders);
    }
}
