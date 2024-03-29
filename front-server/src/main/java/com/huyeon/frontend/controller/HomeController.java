package com.huyeon.frontend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    @GetMapping("/")
    public String mainPage() {
        return "pages/home/main";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "pages/home/login";
    }
    @GetMapping("/signup")
    public String signUpPage() {
        return "pages/home/signup";
    }
    @GetMapping("/reset")
    public String resetPasswordPage() {
        return "pages/home/resetpassword";
    }
}
