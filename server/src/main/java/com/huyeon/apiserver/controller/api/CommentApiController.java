package com.huyeon.apiserver.controller.api;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.BoardComment;
import com.huyeon.apiserver.model.dto.PageReqDto;
import com.huyeon.apiserver.model.entity.Comment;
import com.huyeon.apiserver.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PageReqDto request) {
        List<BoardComment> commentsRes = commentService.getCommentInUser(userDetails.getUser(), request);
        return new ResponseEntity<>(commentsRes, HttpStatus.OK);
    }

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
