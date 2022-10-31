package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.BoardHeaderDto;
import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.board.dto.PageReqDto;
import com.huyeon.superspace.domain.board.entity.Category;
import com.huyeon.superspace.domain.board.entity.history.BoardHistory;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
public class BoardServiceTest {

    @Autowired
    BoardService boardService;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    GroupService groupService;

    @Autowired
    CategoryService categoryService;

    @BeforeAll
    void init() {
        GroupDto request = new GroupDto("테스트그룹", "test-group", "test");
        groupService.createGroup("test@test.com", request);

        CategoryDto categoryDto = new CategoryDto(1L, "테스트 카테고리", -1);
        categoryService.createCategory(categoryDto, "test-group");
    }

    @Test
    @DisplayName("게시글 생성")
    void createBoard() {
        //given
        String email = "test@test.com";
        String groupUrl = "test-group";

        //when, then
        assertDoesNotThrow(() -> boardService.createBoard(email, groupUrl));
    }

    @Test
    @DisplayName("BoardDto 획득")
    void getBoardDto() {
        //given
        Long boardId = createTestBoard();

        //when
        BoardDto boardDto = boardService.getBoardDto(boardId);

        //then
        assertEquals(boardId, boardDto.getId());
        assertEquals("테스트그룹", boardDto.getGroupName());
        assertEquals("test@test.com", boardDto.getAuthor());
    }

    private Long createTestBoard() {
        String email = "test@test.com";
        String groupUrl = "test-group";

        return boardService.createBoard(email, groupUrl);
    }

    @Test
    @DisplayName("게시글 수정")
    void editBoard() {
        //given
        BoardHeaderDto request = editTileRequest();

        //when, then
        assertDoesNotThrow(() -> boardService.editBoard(request));
    }

    private BoardHeaderDto editTileRequest() {
        Long boardId = createTestBoard();
        return BoardHeaderDto.builder()
                .id(boardId)
                .title("WELCOME")
                .target("title")
                .build();
    }

    @Test
    @DisplayName("게시글 삭제")
    void deleteBoard() {
        //given
        Long boardId = createTestBoard();

        //when, then
        assertDoesNotThrow(() -> boardService.removeBoard(boardId));
    }

    @Test
    @DisplayName("게시글 수정이력 확인")
    void findBoardHistory() {
        //given
        BoardHeaderDto request = editTileRequest();
        boardService.editBoard(request);

        //when
        List<BoardHistory> boardHistories = boardService.findBoardHistory(request.getId());

        //then
        assertFalse(boardHistories.isEmpty());
    }

    @Test
    @DisplayName("특정 카테고리의 게시글 조회")
    void getBoardDtoListByCategory() {
        //given
        BoardHeaderDto request = editCategoryRequest();
        boardService.editBoard(request);
        Category category = categoryService.getCategory("테스트 카테고리");

        //when
        List<BoardDto> dtoList = boardService.getBoardResponsesByCategory(category);

        //then
        assertEquals(1, dtoList.size());
        dtoList.forEach(dto -> {
            assertEquals("테스트그룹", dto.getGroupName());
            assertEquals("test@test.com", dto.getAuthor());
        });
    }

    private BoardHeaderDto editCategoryRequest() {
        Long boardId = createTestBoard();

        return BoardHeaderDto.builder()
                .id(boardId)
                .categoryId(2L)
                .target("category")
                .build();
    }

    @Test
    @DisplayName("그룹 내 최신 10개 게시물 조회")
    void getNext10LatestInGroup() {
        //given
        createTestBoard();
        PageReqDto request = new PageReqDto("test-group", LocalDateTime.now(), 0);

        //when
        List<BoardDto> latest = boardService.getNext10LatestInGroup(request);

        //then
        assertEquals(1, latest.size());
        latest.forEach(dto -> {
            assertEquals("테스트그룹", dto.getGroupName());
            assertEquals("test@test.com", dto.getAuthor());
        });
    }

    @Test
    @DisplayName("카테고리 내 최신 10개 게시물 조회")
    void getNext10LatestInCategory() {
        //given
        BoardHeaderDto request = editCategoryRequest();
        boardService.editBoard(request);
        //커밋 전에 조회하면 당연히 조회가 안되므로 5초 여유두고 조회(테스트에서만)
        PageReqDto pageReq = new PageReqDto("테스트 카테고리", LocalDateTime.now().plusSeconds(5), 0);

        //when
        List<BoardDto> latest = boardService.getNext10LatestInCategory(pageReq);

        //then
        assertEquals(1, latest.size());
        latest.forEach(dto -> {
            assertEquals("테스트그룹", dto.getGroupName());
            assertEquals("test@test.com", dto.getAuthor());
        });
    }

    @Test
    @DisplayName("사용자가 작성한 최신 10개 게시물 조회")
    void getNext10LatestInUser() {
        //given
        createTestBoard();
        PageReqDto request = new PageReqDto(null, LocalDateTime.now(), 0);
        String email = "test@test.com";

        //when
        List<BoardDto> latest = boardService.getNext10LatestInUser(request, email);

        //then
        assertEquals(1, latest.size());
        latest.forEach(dto -> {
            assertEquals("테스트그룹", dto.getGroupName());
            assertEquals("test@test.com", dto.getAuthor());
        });
    }
}
