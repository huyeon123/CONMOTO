package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.entity.Comment;
import com.huyeon.apiserver.service.BoardService;
import com.huyeon.apiserver.service.CommentService;
import com.huyeon.apiserver.support.JsonParse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<?> createComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long boardId, @RequestBody Comment comment) {
        Long commentId = commentService.createComment(userDetails.getUsername(), boardId, comment);
        return new ResponseEntity<>(commentId > 0, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> removeComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long commentId) {
        boolean success = commentService.removeComment(userDetails.getUsername(), commentId);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }
}
