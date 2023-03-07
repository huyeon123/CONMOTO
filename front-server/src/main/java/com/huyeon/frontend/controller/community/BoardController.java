package com.huyeon.frontend.controller.community;

import com.huyeon.frontend.aop.RequiredLogin;
import com.huyeon.frontend.aop.refreshAccessTokenAop;
import com.huyeon.frontend.util.Fetch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class BoardController {

    private final Fetch fetch;

    @GetMapping("/{groupUrl}/board/{id}")
    public String boardPage(
            HttpServletRequest request,
            String newAccessToken,
            @PathVariable String groupUrl,
            @PathVariable String id, Model model) {
        fetch.bindResponse(
                "/community/" + groupUrl + "/board/" + id,
                newAccessToken, model
        );
        return "pages/board/board-viewer";
    }

    @RequiredLogin
    @GetMapping("/{groupUrl}/board/editor/{id}")
    public String boardEditPage(
            HttpServletRequest request,
            String newAccessToken,
            @PathVariable String groupUrl,
            @PathVariable String id, Model model) {
        fetch.bindResponse(
                "/community/" + groupUrl + "/board/" + id,
                newAccessToken, model
        );
        return "pages/board/board-modify";
    }

    @RequiredLogin
    @GetMapping("/{groupUrl}/board/editor")
    public String boardCreatePage(
            HttpServletRequest request,
            String newAccessToken,
            @PathVariable String groupUrl,
            Model model) {
        fetch.bindResponse(
                "/community/" + groupUrl + "/board",
                newAccessToken, model
        );
        return "pages/board/board-editor";
    }
}
