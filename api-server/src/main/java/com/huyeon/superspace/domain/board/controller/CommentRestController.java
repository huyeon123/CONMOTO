package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.dto.CommentDto;
import com.huyeon.superspace.domain.board.dto.CommentPreviewDto;
import com.huyeon.superspace.domain.board.service.NewCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentRestController {
    private final NewCommentService commentService;

    @GetMapping("/latest")
    public List<CommentPreviewDto> getComments(
            @RequestParam String memberId,
            @RequestParam Long lastIndex,
            @RequestParam(required = false, defaultValue = "0") int page
    ) {
        return commentService.getNextComment(memberId, lastIndex, page);
    }

    @PostMapping
    public Long createComment(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody CommentDto request
    ) {
        Objects.requireNonNull(request.getBoardId());

        return commentService.createComment(userEmail, request);
    }

    @PutMapping
    public Long editComment(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody CommentDto request
    ) {
        return commentService.editComment(userEmail, request);
    }

    @DeleteMapping("/{id}")
    public void removeComment(@PathVariable Long id) {
        commentService.removeComment(id);
    }
}
