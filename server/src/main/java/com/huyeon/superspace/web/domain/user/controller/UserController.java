package com.huyeon.superspace.web.domain.user.controller;

import com.huyeon.superspace.web.annotation.NotGroupPage;
import com.huyeon.superspace.global.model.UserDetailsImpl;
import com.huyeon.superspace.web.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @NotGroupPage
    @GetMapping("/info")
    public String myPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        UserDto user = new UserDto(userDetails.getUser());
        model.addAttribute("user", user);
        return "pages/user/myinfo";
    }

    @NotGroupPage
    @GetMapping("/board")
    public String myBoardPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model) {
        return "pages/user/mypost";
    }

    @NotGroupPage
    @GetMapping("/comment")
    public String myCommentPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model) {
        return "pages/user/mycomment";
    }

    @NotGroupPage
    @GetMapping("/resign")
    public String resignPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        return "pages/user/resign";
    }

    @NotGroupPage
    @GetMapping("/noty")
    public String notyCenterPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model) {
        return "pages/user/notypage";
    }
}
