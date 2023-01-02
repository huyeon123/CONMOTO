package com.huyeon.frontend.controller;

import com.huyeon.frontend.aop.PurePage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    @PurePage
    @GetMapping("/")
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
}
