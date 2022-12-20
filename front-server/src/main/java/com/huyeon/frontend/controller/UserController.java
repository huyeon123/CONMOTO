package com.huyeon.frontend.controller;

import com.huyeon.frontend.aop.refreshAccessTokenAop;
import com.huyeon.frontend.util.Fetch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final Fetch fetch;

    @GetMapping("/info")
    public String myPage(
            @CookieValue(refreshAccessTokenAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            Model model) {
        fetch.bindResponse(
                "/user/info",
                newAccessToken, model
        );
        return "pages/user/myinfo";
    }

    @GetMapping("/board")
    public String myBoardPage(
            @CookieValue(refreshAccessTokenAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            Model model) {
        fetch.bindResponse(
                "/user/board",
                newAccessToken, model
        );
        return "pages/user/mypost";
    }

    @GetMapping("/comment")
    public String myCommentPage(
            @CookieValue(refreshAccessTokenAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            Model model) {
        fetch.bindResponse(
                "/user/comment",
                newAccessToken, model
        );
        return "pages/user/mycomment";
    }

    @GetMapping("/resign")
    public String resignPage(
            @CookieValue(refreshAccessTokenAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            Model model) {
        fetch.bindResponse(
                "/user/resign",
                newAccessToken, model
        );
        return "pages/user/resign";
    }

    @GetMapping("/noty")
    public String notyCenterPage(
            @CookieValue(refreshAccessTokenAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            Model model) {
        fetch.bindResponse(
                "/user/noty",
                newAccessToken, model
        );
        return "pages/user/notypage";
    }
}
