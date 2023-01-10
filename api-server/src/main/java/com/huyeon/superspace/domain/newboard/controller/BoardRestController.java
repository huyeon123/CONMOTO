package com.huyeon.superspace.domain.newboard.controller;

import com.huyeon.superspace.domain.newboard.dto.BoardDto;
import com.huyeon.superspace.domain.newboard.dto.ContentDto;
import com.huyeon.superspace.domain.newboard.service.NewBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardRestController {

    private final NewBoardService boardService;

    @GetMapping("/{id}")
    public BoardDto getBoard(@PathVariable String id) {
        return boardService.getBoard(id);
    }

    @GetMapping("/latest/{groupUrl}")
    public List<BoardDto> getLatestBoardInGroup(
            @PathVariable String groupUrl,
            @RequestParam int page
    ) {
        return boardService.getNext10LatestInGroup(groupUrl, page);
    }

    @GetMapping("/latest/{categoryName}")
    public List<BoardDto> getLatestBoardInCategory(
            @PathVariable String categoryName,
            @RequestParam int page
    ) {
        return boardService.getNext10LatestInCategory(categoryName, page);
    }

    @GetMapping("/latest/mine")
    public List<BoardDto> getLatestBoardInUser(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestParam int page
    ) {
        return boardService.getNext10LatestInUser(userEmail, page);
    }

    @PostMapping("/board/{groupUrl}/new")
    public String createBoard(
            @PathVariable String groupUrl,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        return boardService.createBoard(groupUrl, userEmail);
    }

    @PutMapping("/edit/title")
    public String updateTitle(@RequestBody BoardDto request) {
        Objects.requireNonNull(request.getId());
        return boardService.saveBoard(request, "TITLE");
    }

    @PutMapping("/edit/description")
    public String updateDescription(@RequestBody BoardDto request) {
        Objects.requireNonNull(request.getId());
        return boardService.saveBoard(request, "DESCRIPTION");
    }

    @PutMapping("/edit/category")
    public String updateCategory(@RequestBody BoardDto request) {
        Objects.requireNonNull(request.getId());
        return boardService.saveBoard(request, "CATEGORY");
    }

    @PutMapping("/edit/status")
    public String updateStatus(@RequestBody BoardDto request) {
        Objects.requireNonNull(request.getId());
        return boardService.saveBoard(request, "STATUS");
    }

    @PutMapping("/edit/tags")
    public String updateTags(@RequestBody BoardDto request) {
        Objects.requireNonNull(request.getId());
        return boardService.saveBoard(request, "TAGS");
    }

    @PutMapping("/edit/content")
    public String saveContent(@RequestBody ContentDto request) {
        Objects.requireNonNull(request.getId());
        return boardService.saveContent(request);
    }

    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable String id) {
        boardService.deleteBoard(id);
    }

    @GetMapping("/content/{id}")
    public ContentDto getContent(@PathVariable String id) {
        return boardService.getContent(id);
    }
}
