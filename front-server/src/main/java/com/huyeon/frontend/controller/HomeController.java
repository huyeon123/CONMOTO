package com.huyeon.frontend.controller;

import com.huyeon.frontend.aop.PurePage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    @PurePage
    @GetMapping
    public String mainPage() {
        return "pages/login";
    }
    @PurePage
    @GetMapping("/signup")
    public String signUpPage() {
        return "pages/signup";
    }
    @PurePage
    @GetMapping("/reset")
    public String resetPasswordPage() {
        return "pages/resetpassword";
    }
    @PurePage
    @GetMapping("/error")
    public String accessDeniedPage() {
        return "pages/AccessDenied";
    }
}
