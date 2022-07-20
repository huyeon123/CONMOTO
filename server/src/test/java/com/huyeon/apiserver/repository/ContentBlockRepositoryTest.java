package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class ContentBlockRepositoryTest {
    @Autowired
    ContentBlockRepository blockRepository;
    @Autowired
    UserService userService;

    @DisplayName("findAllByBoardIdIn Test")
    @Test
    void test_1(){
        List<Long> boards = userService.myBoard("user@test.com")
                .stream()
                .map(Board::getId)
                .collect(Collectors.toList());
        blockRepository.findAllByBoardIdIn(boards).ifPresent(System.out::println);
    }
}
