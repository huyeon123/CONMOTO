package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/{id}")
    public String getBoard(@PathVariable Long id) {
        String board = boardService.getBoard(id);
        if(board == null) return "게시글이 존재하지 않습니다.";
        return board;
    }

    @PostMapping
    public String writeBoard(@RequestBody String newBoard) {
        if(boardService.writeBoard(newBoard)) return "게시글이 생성되었습니다.";
        return "게시글을 작성할 수 없습니다.";
    }

    @PutMapping("/{id}")
    public String post(@PathVariable Long id, @RequestBody String editBoard){
        if(boardService.editBoard(id, editBoard)) return "게시글을 수정했습니다.";
        return "게시글을 수정할 수 없습니다.";
    }

    @DeleteMapping("/{id}")
    public String removeBoard(@PathVariable Long id) {
        if(boardService.removeBoard(id)) return "게시글을 삭제했습니다.";
        return "게시글을 삭제할 수 없습니다.";
    }
}
