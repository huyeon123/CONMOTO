package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping
    public String mainPage() {
        return "Main.html";
    }

    @GetMapping("/signup")
    public String signUpPage() {
        return "redirect:UserController";
    }

}
