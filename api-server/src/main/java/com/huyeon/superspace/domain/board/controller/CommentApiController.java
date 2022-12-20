package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.dto.CommentDto;
import com.huyeon.superspace.domain.board.dto.PageReqDto;
import com.huyeon.superspace.domain.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentApiController {
    private final CommentService commentService;

    @PostMapping("/latest")
    public ResponseEntity<?> getComment(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody PageReqDto request) {
        List<CommentDto> commentsRes = commentService.getCommentInUser(userEmail, request);
        return new ResponseEntity<>(commentsRes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createComment(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestParam Long boardId, @RequestBody CommentDto comment) {
        commentService.createComment(userEmail, boardId, comment);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> editComment(@RequestBody CommentDto comment) {
        commentService.editComment(comment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> removeComment(@RequestParam Long commentId) {
        commentService.removeComment(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
