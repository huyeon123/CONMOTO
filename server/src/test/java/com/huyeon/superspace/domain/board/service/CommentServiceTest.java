package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.dto.BoardHeaderDto;
import com.huyeon.superspace.domain.board.dto.CommentDto;
import com.huyeon.superspace.domain.board.dto.PageReqDto;
import com.huyeon.superspace.domain.board.entity.history.CommentHistory;
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
public class CommentServiceTest {
    @Autowired
    CommentService commentService;

    @Autowired
    BoardService boardService;

    @Autowired
    GroupService groupService;

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
        String groupUrl = "test-group";

        boardId = boardService.createBoard(email, groupUrl);

        BoardHeaderDto changeCategoryReq
                = BoardHeaderDto.builder()
                .id(boardId)
                .categoryId(1L)
                .target("category")
                .build();

        boardService.editBoard(changeCategoryReq);
    }

    @Test
    @DisplayName("댓글 생성")
    void createComment() {
        //given
        String email = "test@test.com";
        CommentDto request = createCommentReq("테스트 댓글입니다.");

        //when, then
        assertDoesNotThrow(() -> commentService.createComment(email, boardId, request));
    }

    private CommentDto createCommentReq(String comment) {
        return CommentDto.builder()
                .comment(comment)
                .build();
    }

    @Test
    @DisplayName("댓글 수정")
    void editComment() {
        //given
        createTestComment();
        CommentDto request = createCommentReq("수정 댓글입니다.");
        request.setId(1L);

        //when, then
        assertDoesNotThrow(() -> commentService.editComment(request));
    }

    private Long createTestComment() {
        String email = "test@test.com";
        CommentDto request = createCommentReq("테스트 댓글입니다.");

        return commentService.createComment(email, boardId, request);
    }

    @Test
    @DisplayName("댓글 수정이력 조회")
    void getCommentHistory() {
        //given
        Long commentId = editTestComment();

        //when
        List<CommentHistory> histories = commentService.commentHistory(commentId);

        //then
        assertFalse(histories.isEmpty());
        histories.forEach(history -> assertEquals("수정 댓글입니다.", history.getPastComment()));
    }

    private Long editTestComment() {
        Long commentId = createTestComment();
        CommentDto request = createCommentReq("수정 댓글입니다.");
        request.setId(commentId);

        commentService.editComment(request);

        return request.getId();
    }

    @Test
    @DisplayName("댓글 삭제")
    void removeComment() {
        //given
        Long commentId = createTestComment();

        //when, then
        assertDoesNotThrow(() -> commentService.removeComment(commentId));
    }

    @Test
    @DisplayName("특정 게시글에 작성된 댓글 조회")
    void getCommentsByBoardId() {
        //given
        createTestComment();

        //when
        List<CommentDto> dtoList = commentService.getCommentsResponseByBoardId(boardId);

        //then
        assertFalse(dtoList.isEmpty());
        dtoList.forEach(dto -> assertEquals("테스트 댓글입니다.", dto.getComment()));
    }

    @Test
    @DisplayName("사용자가 작성한 최신 10개의 댓글 조회")
    void getCommentInUser() {
        //given
        createTestComment();
        String email = "test@test.com";
        PageReqDto request = new PageReqDto(null, LocalDateTime.now().plusSeconds(5), 0);

        //when
        List<CommentDto> comments = commentService.getCommentInUser(email, request);

        //then
        assertFalse(comments.isEmpty());
        comments.forEach(dto -> assertEquals("TEST_USER", dto.getAuthor()));
    }
}
