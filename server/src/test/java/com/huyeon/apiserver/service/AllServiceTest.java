package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.Board;
import com.huyeon.apiserver.model.dto.Comment;
import com.huyeon.apiserver.model.dto.ContentBlock;
import com.huyeon.apiserver.model.dto.User;
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @DisplayName("Scenario 1")
    @Test
    void test_1() {
        //회원 추가
        User user1 = User.builder()
                .name("사용자1")
                .email("user1@huyeon.com")
                .password("user1pw")
                .birthday(LocalDate.of(1997, 10, 21))
                .build();

        User user2 = User.builder()
                .name("사용자2")
                .email("user2@huyeon.com")
                .password("user2pw")
                .build();

        //Client에서는 JSON으로 옴
        userService.signUp(writeJson(user1));
        userService.signUp(writeJson(user2));

        //저장 전에는 ID를 모름
        //클라이언트가 회원가입 후, email과 pw로 로그인을 하면 서버가 ID를 반환해준다고 가정
        user1 = userRepository.findByEmail("user1@huyeon.com").get();
        user2 = userRepository.findByEmail("user2@huyeon.com").get();

        //정보 확인
        System.out.println("userService.userInfo(user1.getId()) = "
                + userService.userInfo(user1.getId()));

        //게시글 작성
        Board board1 = Board.builder()
                .user(user1)
                .title("제목1")
                .status(Board.STATUS.PROCEED)
                .build();

        Board board2 = Board.builder()
                .user(user2)
                .title("제목2")
                .status(Board.STATUS.COMPLETE)
                .build();

        boardService.writeBoard(writeJson(board1));
        boardService.writeBoard(writeJson(board2));

        //작성완료를 누르면 업로드 후 게시글 리스트로 이동
        List<Board> user1Board = boardRepository.findAllByUser(user1);
        List<Board> user2Board = boardRepository.findAllByUser(user2);

        //게시글 확인
        user1Board.forEach(board -> System.out.println(
                board.getUser().getName() + " : " +
                        boardService.getBoard(board.getId())));

        //컨텐츠 블록 생성
        ContentBlock block1 = ContentBlock.builder()
                .board(user2Board.get(0))
                .content("보드2-내용1")
                .build();

        ContentBlock block2 = ContentBlock.builder()
                .board(user2Board.get(0))
                .content("보드2-내용2")
                .build();

        blockService.writeContent(writeJson(block1));
        blockService.writeContent(writeJson(block2));

        //블록 확인
        List<ContentBlock> board2Contents = blockRepository.findAllByBoard(user2Board.get(0));
        board2Contents.forEach(block -> System.out.println(
                blockService.getContentBlock(block.getId())));

        //사용자1 -> 보드2(사용자2) 댓글
        Comment comment1 = Comment.builder()
                .user(user1)
                .board(user2Board.get(0))
                .comment("잼있다!")
                .build();

        //사용자2 -> 보드2(사용자2) 댓글
        Comment comment2 = Comment.builder()
                .user(user2)
                .board(user2Board.get(0))
                .comment("감사합니다(__)")
                .build();

        commentService.writeComment(writeJson(comment1));
        commentService.writeComment(writeJson(comment2));

        //사용자2가 보드2의 댓글 확인
        String board2Comment = commentService.getCommentByBoard(user2Board.get(0));
        System.out.println("board2Comment = " + board2Comment);
    }
}
