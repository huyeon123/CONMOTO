package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.ContentDto;
import com.huyeon.superspace.domain.board.service.NewBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/{groupUrl}/new")
    @ResponseStatus(HttpStatus.CREATED)
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

    @PutMapping("/edit/content") //ContentId가 아닌 BoardId로 받아서 처리할지 고민
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
        return boardService.getContentById(id);
    }
}
