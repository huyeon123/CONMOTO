package com.huyeon.apiserver.controller.api;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.BoardHeaderReqDto;
import com.huyeon.apiserver.model.dto.BoardReqDto;
import com.huyeon.apiserver.model.dto.BoardResDto;
import com.huyeon.apiserver.model.dto.ResMessage;
import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.service.BoardService;
import com.huyeon.apiserver.service.ContentBlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardService boardService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getBoard(@PathVariable Long id) {
        Board board = boardService.getBoard(id);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> createBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String groupUrl) {
        try {
            Long id = boardService.createBoard(userDetails.getUsername(), groupUrl);

            return new ResponseEntity<>(
                    new ResMessage("게시글이 생성되었습니다.", id, true),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ResMessage("게시글 생성에 실패했습니다."),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/latest")
    public ResponseEntity<?> getLatestBoard(@RequestBody BoardReqDto request) {
        if (isRequestFromGroup(request)) {
            return getLatestBoardInGroup(request);
        } else if (isRequestFromCategory(request)) {
            return getLatestBoardInCategory(request);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isRequestFromGroup(BoardReqDto request) {
        return request.getType().equals("GROUP");
    }

    private boolean isRequestFromCategory(BoardReqDto request) {
        return request.getType().equals("CATEGORY");
    }

    private ResponseEntity<?> getLatestBoardInGroup(BoardReqDto request) {
        try {
            List<BoardResDto> newest = boardService.getNext10LatestInGroup(request);
            return new ResponseEntity<>(newest, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> getLatestBoardInCategory(BoardReqDto request) {
        List<BoardResDto> newest = boardService.getNext10LatestInCategory(request);
        return new ResponseEntity<>(newest, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id, @RequestBody BoardHeaderReqDto request) {
        try {
            boardService.editBoard(request);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeBoard(@PathVariable Long id) {
        boolean success = boardService.removeBoard(id);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }
}
