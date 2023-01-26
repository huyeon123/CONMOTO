package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.ContentDto;
import com.huyeon.superspace.domain.board.service.NewBoardService;
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
    public BoardDto getBoard(@PathVariable String id) {
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
}
