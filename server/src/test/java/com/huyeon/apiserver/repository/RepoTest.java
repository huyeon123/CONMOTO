package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.dto.Board;
import com.huyeon.apiserver.model.dto.Comment;
import com.huyeon.apiserver.model.dto.ContentBlock;
import com.huyeon.apiserver.model.dto.User;
import com.huyeon.apiserver.model.dto.history.BoardHistory;
import com.huyeon.apiserver.repository.history.BoardHistoryRepo;
import com.huyeon.apiserver.repository.history.CommentHistoryRepo;
import com.huyeon.apiserver.repository.history.ContentBlockHistoryRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class RepoTest {
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardHistoryRepo boardHistoryRepo;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ContentBlockRepository blockRepository;
    @Autowired
    ContentBlockHistoryRepo blockHistoryRepo;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentHistoryRepo commentHistoryRepo;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @DisplayName("After Mapping_Join Test")
    @Test
    void test_1() {
        User user = userRepository.findById(1L).orElseThrow();
        user.setBirthday(LocalDate.of(1997, 10, 21));
        userRepository.save(user);
        user = userRepository.findById(1L).orElseThrow();

        Board board = Board.builder()
                .user(user)
                .title("King-A")
                .status(Board.STATUS.COMPLETE)
                .build();

        boardRepository.save(board);

        board.setStatus(Board.STATUS.PROCEED);
        boardRepository.save(board);

        List<BoardHistory> boardHistories = boardHistoryRepo.findAllByBoard(board);
        boardHistories.forEach(boardHistory -> System.out.println(
                boardHistory.getBoard().getStatus()
        ));

        ContentBlock content1 = ContentBlock.builder()
                .board(board)
                .content("CONTENT1")
                .build();

        List<ContentBlock> contents = List.of(
                content1,
                ContentBlock.builder()
                        .board(board)
                        .content("CONTENT2")
                        .build()
        );

        blockRepository.saveAll(contents);

        content1.setContent("Update Content 1");
        blockRepository.save(content1);

        //해당 board의 contents가 담겨있음
        blockRepository.findAllByBoard(board)
                .forEach(block -> System.out.println(
                        block.getId() + " " + block.getContent()));

        //해당 블럭의 수정 전 내용과 수정 후 내용이 담겨있음
        blockHistoryRepo.findAllByContentBlock(content1)
                .forEach(block -> System.out.println(
                        block.getId() + " " + block.getPastContent()));

        Comment comment1 = Comment.builder()
                .user(user)
                .board(board)
                .comment("와아아 너무 멋져요")
                .build();
        List<Comment> comments = List.of(
                comment1,
                Comment.builder()
                        .user(user)
                        .board(board)
                        .comment("SO NICE")
                        .build()
        );

        commentRepository.saveAll(comments);

        comment1.setComment("BAD :(");
        commentRepository.save(comment1);

        commentHistoryRepo.findAllByComment(comment1)
                .forEach(history -> System.out.println(
                        history.getId() + " " + history.getPastComment()));

        commentRepository.findAllByUser(user)
                .forEach(comment -> System.out.println(
                        comment.getId() + " " + comment.getComment()));

        commentRepository.findAllByBoard(board)
                .forEach(comment -> System.out.println(
                        comment.getId() + " " + comment.getComment()));
    }
}
