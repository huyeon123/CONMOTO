package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.entity.Board;
import com.huyeon.superspace.domain.board.entity.Tag;
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
public class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

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
    @DisplayName("태그 저장")
    void createTag() {
        //given
        Tag tag = Tag.builder()
                .board(board)
                .tag("태그1")
                .build();

        //when, then
        assertDoesNotThrow(() -> tagRepository.save(tag));
    }

    @Test
    @DisplayName("특정 게시글의 모든 태그 조회")
    void findAllByBoardId(){
        //given
        createTag();
        Long boardId = board.getId();

        //when
        List<Tag> tags = tagRepository.findAllByBoardId(boardId);

        //then
        assertFalse(tags.isEmpty());
        tags.forEach(tag -> assertEquals(boardId, tag.getBoard().getId()));
    }
}
