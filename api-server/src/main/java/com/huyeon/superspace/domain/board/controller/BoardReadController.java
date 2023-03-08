package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.document.TempPost;
import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.ContentDto;
import com.huyeon.superspace.domain.board.service.NewBoardService;
import com.huyeon.superspace.global.exception.PermissionDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardReadController {

    private final NewBoardService boardService;

    @GetMapping("/{id}")
    public BoardDto getBoard(@PathVariable Long id) {
        return boardService.getBoard(id);
    }

    @GetMapping("/latest/group")
    public List<BoardDto> getLatestBoardInGroup(
            @RequestParam String url,
            @RequestParam int page
    ) {
        return boardService.getNext10LatestInGroup(url, page);
    }

    @GetMapping("/latest/category")
    public List<BoardDto> getLatestBoardInCategory(
            @RequestParam String name,
            @RequestParam int page
    ) {
        return boardService.getNext10LatestInCategory(name, page);
    }

    @GetMapping("/latest/mine")
    public List<BoardDto> getLatestBoardInUser(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestParam int page
    ) {
        return boardService.getNext10LatestInUser(userEmail, page);
    }

    @GetMapping("/content/{id}")
    public ContentDto getContent(@PathVariable String id) {
        return boardService.getContentById(id);
    }

    @GetMapping("/temp/{id}")
    public TempPost getTempPost(
            @RequestHeader("X-Authorization-Id") String email,
            @PathVariable Long id
    ) {
        TempPost tempPost = boardService.findTempPostById(id);
        if (tempPost.getAuthor().equals(email)) {
            return tempPost;
        } else {
            log.warn("[접근 제한] 소유자: {} / 요청자: {}", tempPost.getAuthor(), email);
            throw new PermissionDeniedException("올바르지 않은 요청입니다.");
        }
    }
}
