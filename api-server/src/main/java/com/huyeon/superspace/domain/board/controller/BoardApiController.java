package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.dto.*;
import com.huyeon.superspace.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardService boardService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getBoard(@PathVariable Long id) {
        try {
            BoardDto board = boardService.getBoardDto(id);
            return new ResponseEntity<>(board, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{groupUrl}/create")
    public ResponseEntity<?> createBoard(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl) {
        Long id = boardService.createBoard(userEmail, groupUrl);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PostMapping("/latest/group")
    public ResponseEntity<?> getLatestBoardInGroup(
            @RequestBody PageReqDto request) {
        List<BoardDto> latest = boardService.getNext10LatestInGroup(request);
        return new ResponseEntity<>(latest, HttpStatus.OK);
    }

    @PostMapping("/latest/category")
    public ResponseEntity<?> getLatestBoardInCategory(
            @RequestBody PageReqDto request) {
        List<BoardDto> latest = boardService.getNext10LatestInCategory(request);
        return new ResponseEntity<>(latest, HttpStatus.OK);
    }

    @PostMapping("/latest/user")
    public ResponseEntity<?> getLatestBoardInUser(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody PageReqDto request) {
        List<BoardDto> latest = boardService.getNext10LatestInUser(request, userEmail);
        return new ResponseEntity<>(latest, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editBoard(@RequestBody BoardHeaderDto request) {
        boardService.editBoard(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeBoard(@PathVariable Long id) {
        boardService.removeBoard(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
