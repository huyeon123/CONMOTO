package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.entity.Board;
import com.huyeon.superspace.domain.board.entity.Comment;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.service.GroupService;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    GroupService groupService;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    UserRepository userRepository;

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

        Board board = Board.builder()
                .userEmail(email)
                .title("테스트 게시글")
                .build();

        this.board = boardRepository.save(board);
    }

    @Test
    @DisplayName("댓글 저장")
    void createComment(){
        //given
        User user = userRepository.findByEmail("test@test.com").orElseThrow();
        Comment comment = Comment.builder()
                .user(user)
                .board(board)
                .comment("테스트 댓글입니다")
                .build();

        //when, then
        assertDoesNotThrow(() -> commentRepository.save(comment));
    }

    @Test
    @DisplayName("특정 게시글의 작성된 모든 댓글 조회")
    void findAllByBoardId(){
        //given
        createComment();
        Long boardId = board.getId();

        //when
        List<Comment> comments = commentRepository.findAllByBoardId(boardId);

        //then
        assertFalse(comments.isEmpty());
        comments.forEach(comment -> assertEquals(boardId, comment.getBoard().getId()));
    }

    @Test
    @DisplayName("사용자가 작성한 최신 10개 댓글 조회")
    void findNextLatestByUserEmail(){
        //given
        createComment();
        String email = "test@test.com";
        LocalDateTime now = LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(0, 10);

        //when
        List<Comment> comments = commentRepository.findNextLatestByUserEmail(email, now, pageRequest);

        //then
        assertFalse(comments.isEmpty());
        comments.forEach(comment -> assertEquals(email, comment.getUserEmail()));
    }
}
