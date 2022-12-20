package com.huyeon.frontend.controller;

import com.huyeon.frontend.aop.refreshAccessTokenAop;
import com.huyeon.frontend.util.Fetch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/workspace")
public class BoardController {

    private final Fetch fetch;

    @GetMapping("/{groupUrl}/board/{id}")
    public String boardPage(
            @CookieValue(refreshAccessTokenAop.REFRESH_KEY) String refreshToken,
            String newAccessToken,
            @PathVariable String groupUrl,
            @PathVariable Long id, Model model) {
        fetch.bindResponse(
                "/workspace/" + groupUrl + "/board/" + id,
                newAccessToken, model
        );
        return "pages/board";
    }
}
