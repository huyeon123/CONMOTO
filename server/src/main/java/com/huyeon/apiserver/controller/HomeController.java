package com.huyeon.apiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping
    public String mainPage() {
        return "pages/login";
    }

    @GetMapping("/signup")
    public String signUpPage() {
        return "pages/signup";
    }

    @GetMapping("/reset")
    public String resetPasswordPage() {
        return "pages/resetpassword";
    }

    @GetMapping("/error")
    public String accessDeniedPage() {
        return "pages/AccessDenied";
    }
}
