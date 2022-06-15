package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.Board;
import com.huyeon.apiserver.model.User;
import com.huyeon.apiserver.repository.BoardHistoryRepository;
import com.huyeon.apiserver.repository.BoardRepository;
import com.huyeon.apiserver.repository.UserRepository;
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
    @Autowired
    UserRepository userRepository;

    @DisplayName("prePersist")
    @Test
    void test_1() {
        List<Board> all = boardService.getAll();
        all.forEach(System.out::println);
        List<Board> addData = new ArrayList<>();
        addData.add(Board.builder()
                .userId(1L)
                .content("내용999")
                .title("제목999")
                .build());

        boardService.saveAll(addData);

        User user = userRepository.findById(1L).get();
        user.getBoardList().forEach(System.out::println);

    }

}
