package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.Board;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class BoardServiceTest {
    @Autowired
    BoardService boardService;

    @DisplayName("findAll 테스트")
    @Test
    void test_1(){
        List<Board> all = boardService.getAll();
        all.forEach(System.out::println);
    }
}
