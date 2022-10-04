package com.huyeon.apiserver.controller.page;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.UserDto;
import com.huyeon.apiserver.service.UserService;
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

    private final UserService userService;

    //회원정보
    @GetMapping("/info")
    public String myPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        UserDto user = new UserDto(userDetails.getUser());
        model.addAttribute("user", user);
        return "pages/user/myinfo";
    }

    @GetMapping("/board")
    public String myBoardPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model) {
        return "pages/user/mypost";
    }

    @GetMapping("/comment")
    public String myCommentPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model) {
        return "pages/user/mycomment";
    }

    @GetMapping("/resign")
    public String resignPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        return "pages/user/resign";
    }

    @GetMapping("/noty")
    public String notyCenterPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model) {
        return "pages/user/notypage";
    }
}
