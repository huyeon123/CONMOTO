package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.entity.Board;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.group.repository.GroupRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

@SpringBootTest
public class BoardRepositoryTest {
    @Autowired
    BoardRepository boardRepository;

    @Autowired
    GroupRepository groupRepository;

    @BeforeAll
    static void init() {

    }

}
