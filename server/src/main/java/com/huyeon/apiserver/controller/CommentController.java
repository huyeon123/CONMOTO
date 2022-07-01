package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.model.dto.Board;
import com.huyeon.apiserver.model.dto.Comment;
import com.huyeon.apiserver.service.BoardService;
import com.huyeon.apiserver.service.CommentService;
import com.huyeon.apiserver.support.JsonParse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final BoardService boardService;

    @GetMapping("/{id}/comments")
    public String getComments(@PathVariable Long boardId) {
        List<Comment> comments = commentService.getCommentByBoard(boardId);
        return JsonParse.writeJson(comments);
    }
}
