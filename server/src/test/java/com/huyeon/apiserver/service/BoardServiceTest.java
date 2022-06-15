package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.Board;
import com.huyeon.apiserver.repository.BoardHistoryRepository;
import com.huyeon.apiserver.repository.BoardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class BoardServiceTest {
    @Autowired
    BoardService boardService;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardHistoryRepository boardHistoryRepository;

    @DisplayName("prePersist")
    @Test
    void test_1(){
        List<Board> all = boardService.getAll();
        all.forEach(System.out::println);
        List<Board> addData = new ArrayList<>();
        addData.add(Board.builder()
                .content("내용999")
                .title("제목999")
                .build());

        boardService.saveAll(addData);
    }

    @DisplayName("")
    @Test
    void test_2() {
        Board board = Board.builder()
                .title("제목TEST")
                .content("내용TEST")
                .build();
        boardRepository.save(board);
        boardHistoryRepository.findAll().forEach(System.out::println);
    }
}
