package com.huyeon.apiserver.controller.api;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.BoardHeaderReqDto;
import com.huyeon.apiserver.model.dto.BoardResDto;
import com.huyeon.apiserver.model.dto.PageReqDto;
import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardService boardService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getBoard(@PathVariable Long id) {
        Optional<Board> board = boardService.getBoard(id);
        if (board.isPresent()) {
            return new ResponseEntity<>(board, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<?> createBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String groupUrl) {
        Long id = boardService.createBoard(userDetails.getUsername(), groupUrl);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PostMapping("/latest/group")
    public ResponseEntity<?> getLatestBoardInGroup(
            @RequestBody PageReqDto request) {
        List<BoardResDto> latest = boardService.getNext10LatestInGroup(request);
        return new ResponseEntity<>(latest, HttpStatus.OK);
    }

    @PostMapping("/latest/category")
    public ResponseEntity<?> getLatestBoardInCategory(
            @RequestBody PageReqDto request) {
        List<BoardResDto> latest = boardService.getNext10LatestInCategory(request);
        return new ResponseEntity<>(latest, HttpStatus.OK);
    }

    @PostMapping("/latest/user")
    public ResponseEntity<?> getLatestBoardInUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody PageReqDto request) {
        List<BoardResDto> latest = boardService.getNext10LatestInUser(request, userDetails.getUser());
        return new ResponseEntity<>(latest, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editBoard(@RequestBody BoardHeaderReqDto request) {
        boardService.editBoard(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeBoard(@PathVariable Long id) {
        boolean success = boardService.removeBoard(id);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }
}
