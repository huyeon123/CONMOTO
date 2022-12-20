package com.huyeon.superspace.domain.group.dto;

import com.huyeon.superspace.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberDtoTest {
    @Test
    @DisplayName("MemberDto 생성 - AllArgsConstructor")
    void create(){
        //when, then
        assertDoesNotThrow(() -> new MemberDto("TEST_USER", "test@test.com", "일반 멤버"));
    }

    @Test
    @DisplayName("MemberDto 생성 - User, groupRole 인자")
    void createWithUserAndRole(){
        //given
        User user = User.builder()
                .email("test@test.com")
                .name("TEST_USER")
                .password("12345")
                .build();

        String groupRole = "일반 멤버";

        //when
        MemberDto memberDto = new MemberDto(user, groupRole);

        //then
        assertEquals(user.getEmail(), memberDto.getEmail());
        assertEquals(user.getName(), memberDto.getName());
        assertEquals(groupRole, memberDto.getGroupRole());
    }
}
