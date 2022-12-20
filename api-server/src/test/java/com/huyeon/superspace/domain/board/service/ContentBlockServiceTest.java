package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.dto.ContentDto;
import com.huyeon.superspace.domain.board.entity.Board;
import com.huyeon.superspace.domain.board.entity.history.ContentBlockHistory;
import com.huyeon.superspace.domain.board.repository.BoardRepository;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
public class ContentBlockServiceTest {
    @Autowired
    ContentBlockService blockService;

    @Autowired
    GroupService groupService;

    @Autowired
    BoardRepository boardRepository;

    Long boardId;

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

        boardId = boardRepository.save(testBoard).getId();
    }

    @Test
    @DisplayName("컨텐츠 블록 생성")
    void createContent() {
        //given: boardId

        //when, then
        assertDoesNotThrow(() -> blockService.createContent(boardId));
    }

    @Test
    @DisplayName("컨텐츠 내용 수정")
    void writeContents() {
        //given
        Long contentId = blockService.createContent(boardId);
        ContentDto request = new ContentDto("테스트 내용입니다!");

        //when
        assertDoesNotThrow(() -> blockService.writeContents(contentId, request));
    }

    @Test
    @DisplayName("컨텐츠 삭제")
    void removeContent() {
        //given
        Long contentId = blockService.createContent(boardId);

        //when, then
        assertDoesNotThrow(() -> blockService.removeContent(contentId));
    }

    @Test
    @DisplayName("특정 게시글의 최초 3개의 컨텐츠 내용 조회")
    void getSummaryContentResByBoardId() {
        //given
        createTestContent();

        //when
        List<ContentDto> contents = blockService.getSummaryContentResByBoardId(boardId);

        //then
        assertFalse(contents.isEmpty());
        assertTrue(contents.size() <= 3);
        contents.forEach(dto -> assertEquals("테스트 내용입니다!", dto.getContent()));
    }

    private Long createTestContent() {
        Long contentId = blockService.createContent(boardId);
        ContentDto request = new ContentDto("테스트 내용입니다!");
        blockService.writeContents(contentId, request);

        return contentId;
    }

    @Test
    @DisplayName("특정 게시글의 모든 컨텐츠 내용 조회")
    void getContentResponseByBoardId() {
        //given
        createTestContent();

        //when
        List<ContentDto> contents = blockService.getContentResponseByBoardId(boardId);

        //then
        assertFalse(contents.isEmpty());
        contents.forEach(dto -> assertEquals("테스트 내용입니다!", dto.getContent()));
    }

    @Test
    @DisplayName("컨텐츠 수정 이력 조회")
    void getContentHistory(){
        //given
        Long contentId = createTestContent();

        //when
        List<ContentBlockHistory> histories = blockService.contentHistory(contentId);

        //then
        assertFalse(histories.isEmpty());
        histories.forEach(history -> assertEquals(contentId, history.getBlockId()));
    }
}
