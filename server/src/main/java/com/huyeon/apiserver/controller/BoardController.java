package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.CommentDto;
import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.Comment;
import com.huyeon.apiserver.model.entity.ContentBlock;
import com.huyeon.apiserver.service.BoardService;
import com.huyeon.apiserver.service.CommentService;
import com.huyeon.apiserver.service.ContentBlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final ContentBlockService blockService;
    private final CommentService commentService;

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

            List<CommentDto> comments = mapToCommentDto(commentService.getCommentByBoardId(board.getId()));
            model.addAttribute("comments", comments);

            List<ContentBlock> contents =
                    blockService.getContentBlockByBoardId(board.getId());
            model.addAttribute("contents", contents);
        });

        return "board";
    }

    protected List<CommentDto> mapToCommentDto(List<Comment> comments) {
        return comments.stream()
                .map(comment -> CommentDto.builder()
                        .id(comment.getId())
                        .author(comment.getUserEmail())
                        .date(calculateUploadTime(comment.getUpdatedAt()))
                        .body(comment.getComment())
                        .build())
                .collect(Collectors.toList());
    }

    protected String calculateUploadTime(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        Duration time = Duration.between(createdAt, now);
        long seconds = time.getSeconds();
        if (seconds < 60) return seconds + "초 전";
        if (seconds < 3600) return (seconds / 60) + "분 전";
        if (seconds < 86_400) return (seconds / 3600) + "시간 전";

        Period date = Period.between(createdAt.toLocalDate(), now.toLocalDate());
        if(date.getDays() < 7) return date.getDays() + "일 전";
        if(date.getDays() < 30) return (date.getDays() / 7) + "주 전";
        if(date.getMonths() < 12) return date.getMonths() + "달 전";
        return date.getYears() + "년 전";
    }
}
