package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.UserSignUpReq;
import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.Comment;
import com.huyeon.apiserver.model.entity.ContentBlock;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.repository.BoardRepository;
import com.huyeon.apiserver.repository.ContentBlockRepository;
import com.huyeon.apiserver.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.huyeon.apiserver.support.JsonParse.writeJson;

@SpringBootTest
public class AllServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    BoardService boardService;
    @Autowired
    ContentBlockService blockService;
    @Autowired
    CommentService commentService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    ContentBlockRepository blockRepository;
    @Autowired
    AuthService authService;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @DisplayName("Scenario 1")
    @Test
    void test_1() {
        //회원 추가
        UserSignUpReq user1 = UserSignUpReq.builder()
                .name("사용자1")
                .email("user1@huyeon.com")
                .password("user1pw")
                .birthday(LocalDate.of(1997, 10, 21))
                .build();

        UserSignUpReq user2 = UserSignUpReq.builder()
                .name("사용자2")
                .email("user2@huyeon.com")
                .password("user2pw")
                .build();

        authService.signUp(user1);
        authService.signUp(user2);

        //저장 전에는 ID를 모름
        //클라이언트가 회원가입 후, email과 pw로 로그인을 하면 서버가 ID를 반환해준다고 가정
        user1 = userRepository.findByEmail("user1@huyeon.com").get();
        user2 = userRepository.findByEmail("user2@huyeon.com").get();

        //정보 확인
        System.out.println("userService.userInfo(user1.getId()) = "
                + userService.userInfo(user1.getId()));

        //게시글 작성
        Board board1 = Board.builder()
                .userId(user1.getId())
                .title("제목1")
                .status(Board.STATUS.PROCEED)
                .build();

        Board board2 = Board.builder()
                .userId(user2.getId())
                .title("제목2")
                .status(Board.STATUS.COMPLETE)
                .build();

        boardService.writeBoard(writeJson(board1));
        boardService.writeBoard(writeJson(board2));

        //작성완료를 누르면 업로드 후 게시글 리스트로 이동
        List<Board> user1Board = boardRepository.findAllByUserId(user1.getId()).get();
        List<Board> user2Board = boardRepository.findAllByUserId(user2.getId()).get();

        board1 = user1Board.get(0);
        board2 = user2Board.get(0);

        //게시글 확인
        user1Board.forEach(board -> System.out.println(
                board.getUserId() + " : " +
                        boardService.getBoard(board.getId()).orElse(new Board())));

        //컨텐츠 블록 생성
        ContentBlock block1 = ContentBlock.builder()
                .boardId(board1.getId())
                .content("보드2-내용1")
                .build();

        ContentBlock block2 = ContentBlock.builder()
                .boardId(board2.getId())
                .content("보드2-내용2")
                .build();

        blockService.writeContent(writeJson(block1));
        blockService.writeContent(writeJson(block2));

        //블록 확인
        List<ContentBlock> board2Contents = blockRepository.findAllByBoardId(board2.getId()).get();
        board2Contents.forEach(block -> System.out.println(
                blockService.getContentBlock(block.getId())));

        //사용자1 -> 보드2(사용자2) 댓글
        Comment comment1 = Comment.builder()
                .userId(user1.getId())
                .boardId(user2Board.get(0).getId())
                .comment("잼있다!")
                .build();

        //사용자2 -> 보드2(사용자2) 댓글
        Comment comment2 = Comment.builder()
                .userId(user2.getId())
                .boardId(user2Board.get(0).getId())
                .comment("감사합니다(__)")
                .build();

        commentService.writeComment(writeJson(comment1));
        commentService.writeComment(writeJson(comment2));

        //사용자2가 보드2의 댓글 확인(리스트)
        List<Comment> board2Comment = commentService.getCommentByBoard(board2.getId());
        System.out.println("board2Comment = " + board2Comment);

        //사용자2 회원정보 수정
        user2.setBirthday(LocalDate.of(2022,6,7));
        userService.editInfo(3L, writeJson(user2)); //로그인 되어있으므로 Path Variable을 통해 id 넘어옴
        System.out.println("변경된 사용자2 = " + userService.userInfo(3L));

        //보드2 수정
        board2.setTitle("곧 삭제될 게시글입니다.");
        boardService.editBoard(7L, writeJson(board2));

        //블록2 수정
        block2 = board2Contents.get(0);
        block2.setContent("저는 이만 떠납니다.");
        blockService.editContent(2L, writeJson(block2));

        //댓글2 수정
        comment2 = board2Comment.get(1);
        comment2.setComment("저 곧 탈퇴해요 수고하세용");
        commentService.editComment(2L, writeJson(comment2));

        //정보를 바꿔도 ID는 동일하므로 변경된 내용으로 조회가 잘 됨
        board2Comment = commentService.getCommentByBoard(board2.getId());
        System.out.println("edit comment = " + board2Comment);

        //사용자2 수정이력 확인
        System.out.println("사용자2 변경이력 = " + userService.myInfoHistory(3L));

        //사용자2 탈퇴
        userService.resign(3L);
        System.out.println("게시글2 정보 = " + boardService.getBoard(7L).orElse(new Board()));

        //사용자1의 게시글, 댓글확인
        System.out.println("사용자1 작성 게시글= " + userService.myBoard(2L));
        System.out.println("사용자1 작성 댓글 = " + userService.myComment(2L));
    }

    @DisplayName("Delete Error Test")
    @Test
    void test_2() {
        UserSignUpReq user1 = UserSignUpReq.builder()
                .name("사용자1")
                .email("user1@huyeon.com")
                .password("user1pw")
                .birthday(LocalDate.of(1997, 10, 21))
                .build();

        UserSignUpReq user2 = UserSignUpReq.builder()
                .name("사용자2")
                .email("user2@huyeon.com")
                .password("user2pw")
                .build();

        authService.signUp(user1);
        authService.signUp(user2);
        user1 = userRepository.findByEmail("user1@huyeon.com").get();
        user2 = userRepository.findByEmail("user2@huyeon.com").get();

        Board board1 = Board.builder()
                .userId(user1.getId())
                .title("제목1")
                .status(Board.STATUS.PROCEED)
                .build();

        boardService.writeBoard(writeJson(board1));

        System.out.println("userService.myBoard(user1.getId()) = " + userService.myBoard(user1.getId()));

        userService.resign(user1.getId());

        System.out.println("userService.myBoard(user2.getId()) = " + userService.myBoard(user2.getId()));
    }
}
