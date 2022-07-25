package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.ContentBlock;
import com.huyeon.apiserver.service.BoardService;
import com.huyeon.apiserver.service.ContentBlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final ContentBlockService blockService;

    @GetMapping("/{id}")
    public String boardPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id, Model model) {
        Optional<Board> optBoard = boardService.getBoard(id);

        Board check = optBoard.orElse(new Board());
        if (!check.getUserEmail().equals(userDetails.getUsername())) {
            return "AccessDenied";
        }

        optBoard.ifPresent(board -> {
            model.addAttribute("title", board.getTitle());
            model.addAttribute("status", board.getStatus());

            List<ContentBlock> contents =
                    blockService.getContentBlockByBoardId(board.getId());
            model.addAttribute("contents", contents);
        });

        return "board";
    }
}
