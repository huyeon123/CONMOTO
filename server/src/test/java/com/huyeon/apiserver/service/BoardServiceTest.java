package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.Board;
import com.huyeon.apiserver.model.dto.ContentBlock;
import com.huyeon.apiserver.model.dto.User;
import com.huyeon.apiserver.model.dto.history.BoardHistory;
import com.huyeon.apiserver.repository.BoardRepository;
import com.huyeon.apiserver.repository.ContentBlockRepository;
import com.huyeon.apiserver.repository.UserRepository;
import com.huyeon.apiserver.repository.history.BoardHistoryRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class BoardServiceTest {
    @Autowired
    BoardService boardService;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardHistoryRepo boardHistoryRepo;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ContentBlockRepository blockRepository;

    @DisplayName("After Mapping")
    @Test
    void test_2() {
        User user = userRepository.findById(1L).orElseThrow();
        Board board = Board.builder()
                .user(user)
                .title("King-A")
                .status(Board.STATUS.COMPLETE)
                .build();

        boardRepository.save(board);

        ContentBlock contentBlock = ContentBlock.builder()
                .board(board)
                .content("TESTESTEST")
                .build();

        blockRepository.save(contentBlock);

        Optional<Board> optionalBoard = boardRepository.findById(6L);
        List<ContentBlock> contents = blockRepository.findAllByBoard(optionalBoard.orElse(new Board()));

        contents.forEach(System.out::println);

        System.out.println(boardHistoryRepo.findById(1L).orElse(new BoardHistory()));

        boardRepository.findAll().forEach(System.out::println);
    }

}
