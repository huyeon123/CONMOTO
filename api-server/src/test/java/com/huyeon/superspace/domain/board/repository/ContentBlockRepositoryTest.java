package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.entity.Board;
import com.huyeon.superspace.domain.board.entity.ContentBlock;
import com.huyeon.superspace.domain.board.service.ContentBlockService;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.service.GroupService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
public class ContentBlockRepositoryTest {
    @Autowired
    ContentBlockRepository blockRepository;

    @Autowired
    GroupService groupService;

    @Autowired
    BoardRepository boardRepository;

    Board board;

    @BeforeAll
    void init() {
        createTestGroup();
        createTestBoard();
    }

    private void createTestGroup() {
        GroupDto request = new GroupDto("테스트그룹", "test-group", "test");
        groupService.createGroup("test@test.com", request);
    }

    private void createTestBoard() {
        String email = "test@test.com";

        Board testBoard = Board.builder()
                .userEmail(email)
                .title("테스트 게시글")
                .build();

        board = boardRepository.save(testBoard);
    }

    @Test
    @DisplayName("컨텐츠 저장")
    void createContent(){
        //given
        ContentBlock block = ContentBlock.builder()
                .board(board)
                .content("테스트 내용입니다.")
                .build();

        //when, then
        assertDoesNotThrow(() -> blockRepository.save(block));
    }

    @Test
    @DisplayName("특정 게시글의 모든 컨텐츠 내용 조회")
    void findAllByBoardId(){
        //given
        createContent();
        Long boardId = board.getId();

        //when
        List<ContentBlock> contents = blockRepository.findAllByBoardId(boardId);

        //then
        assertFalse(contents.isEmpty());
        contents.forEach(content -> assertEquals(boardId, content.getBoard().getId()));
    }
    
    @Test
    @DisplayName("특정 게시글의 최초 컨텐츠 3개 조회")
    void findTop3ByBoardId(){
        //given
        createContent();
        Long boardId = board.getId();

        //when
        List<ContentBlock> contents = blockRepository.findTop3ByBoardId(boardId);

        //then
        assertFalse(contents.isEmpty());
        assertTrue(contents.size() <= 3);
        contents.forEach(content -> assertEquals(boardId, content.getBoard().getId()));
    }
}
