package com.huyeon.superspace.web.domain.user.controller;

import com.huyeon.superspace.domain.user.service.UserService;
import com.huyeon.superspace.web.annotation.NotGroupPage;
import com.huyeon.superspace.web.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @NotGroupPage
    @GetMapping("/info")
    public Map<String, Object> myPage(@RequestHeader("X-Authorization-Id") String userEmail) {
        Map<String, Object> response = new HashMap<>();
        UserDto user = userService.getUser(userEmail);
        response.put("user", user);
        return response;
    }
}
