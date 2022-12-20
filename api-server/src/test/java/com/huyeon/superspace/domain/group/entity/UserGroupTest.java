package com.huyeon.superspace.domain.group.entity;

import com.huyeon.superspace.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UserGroupTest {
    @Test
    @DisplayName("UserGroup 생성 테스트")
    void create(){
        //when, then
        assertDoesNotThrow(() -> {
            UserGroup.builder()
                    .user(new User())
                    .group(new WorkGroup())
                    .build();
        });
    }
}
