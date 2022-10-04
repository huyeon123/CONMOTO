package com.huyeon.apiserver.controller.api;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.CommentDto;
import com.huyeon.apiserver.model.dto.PageReqDto;
import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.Comment;
import com.huyeon.apiserver.service.BoardService;
import com.huyeon.apiserver.service.CommentService;
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
public class CommentApiController {
    private final BoardService boardService;
    private final CommentService commentService;

    @PostMapping("/latest")
    public ResponseEntity<?> getComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PageReqDto request) {
        List<CommentDto> commentsRes = commentService.getCommentInUser(userDetails.getUser(), request);
        return new ResponseEntity<>(commentsRes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long boardId, @RequestBody CommentDto comment) {
        Board board = boardService.getBoard(boardId);
        commentService.createComment(userDetails.getUser(), board, comment);
        return new ResponseEntity<>(HttpStatus.OK);
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
