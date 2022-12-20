package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.entity.Comment;
import com.huyeon.superspace.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class CommentDtoTest {
    @Test
    @DisplayName("CommentDto 생성")
    void createDto(){
        //given
        User user = User.builder()
                .name("TEST_USER")
                .email("test@test.com")
                .password("12345")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .user(user)
                .comment("댓글")
                .build();

        //when, then
        assertDoesNotThrow(() -> new CommentDto(comment));
    }
}
