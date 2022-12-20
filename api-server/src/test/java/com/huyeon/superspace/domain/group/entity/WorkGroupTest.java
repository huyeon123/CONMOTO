package com.huyeon.superspace.domain.group.entity;

import com.huyeon.superspace.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorkGroupTest {
    @Test
    @DisplayName("NonNull 어노테이션 테스트")
    void nonNullTest() {
        //when, then
        assertThrows(NullPointerException.class,
                () -> WorkGroup.builder()
                        .build()
        );
    }

    @Test
    @DisplayName("Builder 테스트")
    void builderTest() {
        //when, then
        assertDoesNotThrow(() -> {
            WorkGroup.builder()
                    .name("테스트그룹")
                    .urlPath("test-group")
                    .description("테스트입니다.")
                    .owner(new User())
                    .build();
        });
    }
}
