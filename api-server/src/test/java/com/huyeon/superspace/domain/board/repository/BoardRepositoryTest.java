package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.entity.Board;
import com.huyeon.superspace.domain.board.entity.Category;
import com.huyeon.superspace.domain.board.entity.history.BoardHistory;
import com.huyeon.superspace.domain.board.repository.history.BoardHistoryRepo;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.group.service.GroupService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
public class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    BoardHistoryRepo boardHistoryRepo;

    @Autowired
    GroupService groupService;

    @Autowired
    CategoryRepository categoryRepository;

    @BeforeAll
    void init() {
        GroupDto request = new GroupDto("테스트그룹", "test-group", "test");
        groupService.createGroup("test@test.com", request);
    }

    @Test
    @DisplayName("게시글 저장")
    void save() {
        //given
        Board board = createTestBoard();

        //when, then
        assertDoesNotThrow(() -> boardRepository.save(board));
    }

    private Board createTestBoard() {
        return Board.builder()
                .userEmail("test@test.com")
                .title("테스트 게시글")
                .build();
    }

    @Test
    @DisplayName("특정 카테고리에 속하는 게시글 조회")
    void findAllByCategory() {
        //given
        Board testBoard = createTestBoard();
        Category category = categoryRepository.findById(1L).orElseThrow();
        testBoard.setCategory(category);
        boardRepository.save(testBoard);

        //when
        List<Board> boards = boardRepository.findAllByCategory(category);

        //then
        assertFalse(boards.isEmpty());
        boards.forEach(board -> assertEquals(category.getName(), board.getCategoryName()));
    }

    @Test
    @DisplayName("그룹 내 최신 10개 게시글 조회")
    void findNextTenLatestByGroup() {
        //given
        WorkGroup group = groupService.getGroupByUrl("test-group");
        Board testBoard = createTestBoard();
        testBoard.setGroup(group);
        boardRepository.save(testBoard);

        //when
        List<Board> latest = boardRepository.findNextTenLatest(group, LocalDateTime.now(), PageRequest.of(0, 10));

        //then
        assertFalse(latest.isEmpty());
        latest.forEach(board -> assertEquals(group.getName(), board.getGroupName()));
    }

    @Test
    @DisplayName("카테고리 내 최신 10개 게시글 조회")
    void findNextTenLatestByCategory() {
        //given
        Board testBoard = createTestBoard();
        Category category = categoryRepository.findById(1L).orElseThrow();
        testBoard.setCategory(category);
        boardRepository.save(testBoard);

        //when
        List<Board> latest = boardRepository.findNextTenLatest(category, LocalDateTime.now(), PageRequest.of(0, 10));

        //then
        assertFalse(latest.isEmpty());
        latest.forEach(board -> assertEquals(category.getName(), board.getCategoryName()));
    }

    @Test
    @DisplayName("사용자가 작성한 최신 10개 게시글 조회")
    void findNextTenLatestByEmail() {
        //given
        save();
        String email = "test@test.com";

        //when
        List<Board> latest = boardRepository.findNextTenLatest(email, LocalDateTime.now(), PageRequest.of(0, 10));

        //then
        assertFalse(latest.isEmpty());
        latest.forEach(board -> assertEquals(email, board.getUserEmail()));
    }

    @Test
    @DisplayName("게시글 변경 이력 조회")
    void findAllByBoardId(){
        //given
        Board board = createTestBoard();
        board = boardRepository.save(board);

        boardRepository.flush();

        board.setDescription("테스트 설명글");
        board = boardRepository.save(board);

        boardRepository.flush();

        Long boardId = board.getId();

        //when
        List<BoardHistory> histories = boardHistoryRepo.findAllByBoardId(boardId);

        //then
        assertFalse(histories.isEmpty());
        histories.forEach(history -> assertEquals(boardId, history.getBoardId()));
    }
}
