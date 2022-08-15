package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.UserSignInReq;
import com.huyeon.apiserver.model.dto.UserSignUpReq;
import com.huyeon.apiserver.model.dto.ResMessage;
import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.ContentBlock;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.huyeon.apiserver.support.JsonParse.*;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원정보
    @GetMapping("/profile")
    public String userInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model) {
        User user = userDetails.getUser();
        model.addAttribute("email", user.getEmail());
        model.addAttribute("name", user.getName());
        model.addAttribute("birthday", user.getBirthday());
        return "editprofile";
    }

    @GetMapping("/info")
    public String myFeed(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        if(userDetails.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) return "admin";

        model.addAttribute("username", userDetails.getUser().getName());

        Map<Board, List<ContentBlock>> boardAndContents = userService.myBoardAndContents(userDetails.getUsername());
        model.addAttribute("boardAndContents", boardAndContents);

        model.addAttribute("comments", userService.myComment(userDetails.getUsername()));
        return "myinfo";
    }

    @GetMapping("/resign")
    public String resignPage() {
        return "resign";
    }
}
