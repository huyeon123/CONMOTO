package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.ResMessage;
import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            return new ResponseEntity<>(board.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("게시글을 찾을 수 없습니다.", HttpStatus.OK);
        }
    }

    @GetMapping
    public ResponseEntity<ResMessage> createBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long id;
        ResMessage response = new ResMessage();
        Board newBoard = new Board();
        newBoard.setUserEmail(userDetails.getUsername());
        newBoard.setStatus(Board.STATUS.READY);

        if ((id = boardService.createBoard(newBoard)) >= 0) {
            response.setMessage("게시글이 생성되었습니다.");
            response.setData(id);
            response.setSuccess(true);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.setMessage("게시글을 작성할 수 없습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResMessage> editBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id, @RequestBody Board editBoard) {
        editBoard.setId(id);
        ResMessage response = new ResMessage();
        if (boardService.editBoard(userDetails.getUsername(), editBoard)){
            response.setMessage("게시글을 수정했습니다.");
            response.setSuccess(true);
        } else {
            response.setMessage("게시글을 수정할 수 없습니다.");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeBoard(@PathVariable Long id) {
        if (boardService.removeBoard(id)){
            return new ResponseEntity<>("게시글을 삭제했습니다.", HttpStatus.OK);
        }
        return new ResponseEntity<>("게시글을 삭제할 수 없습니다.", HttpStatus.OK);
    }
}
