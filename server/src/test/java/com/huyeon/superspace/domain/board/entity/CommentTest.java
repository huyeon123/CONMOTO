package com.huyeon.superspace.domain.board.entity;

import com.huyeon.superspace.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentTest {
    @Test
    @DisplayName("Comment 생성")
    void build() {
        //when, then
        assertDoesNotThrow(() -> Comment.builder()
                .comment("테스트 내용")
                .build());

        assertDoesNotThrow(() -> new Comment());
    }

    @Test
    @DisplayName("User 등록 후 name, email 조회")
    void commentUserTest() {
        //given
        User user = User.builder()
                .name("TEST_USER")
                .email("test@test.com")
                .password("12345")
                .build();

        //when
        Comment comment = Comment.builder()
                .user(user)
                .comment("테스트 내용")
                .build();

        //then
        assertEquals(user.getName(), comment.getUserName());
        assertEquals(user.getEmail(), comment.getUserEmail());
    }
}
