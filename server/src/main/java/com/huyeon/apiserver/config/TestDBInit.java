package com.huyeon.apiserver.config;

import com.huyeon.apiserver.model.Authority;
import com.huyeon.apiserver.model.dto.UserSignUpReq;
import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.Comment;
import com.huyeon.apiserver.model.entity.ContentBlock;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.repository.BoardRepository;
import com.huyeon.apiserver.repository.CommentRepository;
import com.huyeon.apiserver.repository.ContentBlockRepository;
import com.huyeon.apiserver.repository.UserRepository;
import com.huyeon.apiserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TestDBInit implements CommandLineRunner {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ContentBlockRepository blockRepository;

    @Override
    public void run(String... args) throws Exception {
        authService.signUp(UserSignUpReq.builder()
                .name("TEST_USER")
                .email("user@test.com")
                .password("1234")
                .birthday(LocalDate.of(2022, 7, 15))
                .build());

        authService.signUp(UserSignUpReq.builder()
                .name("TEST_SUBSCRIBER")
                .email("sub@test.com")
                .password("1234")
                .birthday(LocalDate.of(2022, 7, 15))
                .build());

        authService.signUp(UserSignUpReq.builder()
                .name("TEST_ADMIN")
                .email("admin@test.com")
                .password("1234")
                .birthday(LocalDate.of(2022, 7, 15))
                .build());

        Optional<User> admin = userRepository.findByEmail("admin@test.com");
        admin.ifPresent(user -> {
            user.getAuthorities().add(new Authority(Authority.ROLE_ADMIN));
            userRepository.save(user);
        });

        boardRepository.save(Board.builder()
                .userEmail("user@test.com")
                .title("testTitle")
                .status(Board.STATUS.READY)
                .build());

        blockRepository.save(ContentBlock.builder()
                .boardId(1L)
                .content("NICE CONTENT")
                .build());

        commentRepository.save(Comment.builder()
                .boardId(1L)
                .userEmail("user@test.com")
                .comment("GREAT")
                .build());
    }
}
