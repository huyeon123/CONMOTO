package com.huyeon.apiserver.model.listener;

import com.huyeon.apiserver.model.dto.Board;
import com.huyeon.apiserver.model.dto.Comment;
import com.huyeon.apiserver.model.dto.ContentBlock;
import com.huyeon.apiserver.model.dto.User;
import com.huyeon.apiserver.repository.BoardRepository;
import com.huyeon.apiserver.repository.CommentRepository;
import com.huyeon.apiserver.repository.ContentBlockRepository;
import com.huyeon.apiserver.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
public class ListenerTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ContentBlockRepository contentBlockRepository;

    @DisplayName("HistoryListener TEST")
    @Test
    void test_1(){
        User user = User.builder()
                .name("Strong-man")
                .email("TTTT@asg.com")
                .password("1q2w3e4r!")
                .birthday(LocalDate.of(1997, 10, 21))
                .build();

        userRepository.save(user);

        Board board = Board.builder()
                .userId(2L)
                .title("STRONG!!")
                .content("hello")
                .build();

        boardRepository.save(board);

        ContentBlock contentBlock = ContentBlock.builder()
                .boardId(5L)
                .content("Interesting Contents")
                .build();

        contentBlockRepository.save(contentBlock);

        Comment comment = Comment.builder()
                .boardId(5L)
                .userId(1L)
                .comment("So LAUGH!!")
                .build();

        commentRepository.save(comment);
    }
}
