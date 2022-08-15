package com.huyeon.apiserver.config;

import com.huyeon.apiserver.model.dto.GroupDto;
import com.huyeon.apiserver.model.entity.*;
import com.huyeon.apiserver.model.dto.UserSignUpReq;
import com.huyeon.apiserver.repository.*;
import com.huyeon.apiserver.service.AuthService;
import com.huyeon.apiserver.service.GroupManagerService;
import com.huyeon.apiserver.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class TestDBInit implements CommandLineRunner {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ContentBlockRepository blockRepository;
    private final GroupService groupService;
    private final GroupManagerService managerService;
    private final UserGroupRepository userGroupRepository;
    private final CategoryRepository categoryRepository;

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

        User user = userRepository.findByEmail("user@test.com").orElseThrow();

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

        GroupDto testGroup = GroupDto.builder()
                .name("TEST GROUP")
                .url("test-group")
                .description("어서오세요")
                .build();

        Groups group = groupService.createGroup(user, testGroup);
        managerService.registerUserAsManager(user, group);

        userGroupRepository.save(UserGroup.builder()
                .user(user)
                .group(group)
                .build());

        categoryRepository.save(Category.builder()
                .group(group)
                .name("==최상위 카테고리==")
                .parent(null)
                .build());
    }
}
