package com.huyeon.superspace.domain.group.entity;

import com.huyeon.superspace.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GroupManagerTest {
    @Test
    @DisplayName("GroupManager 생성 테스트")
    void create(){
        //when
        assertDoesNotThrow(() -> GroupManager.builder()
                .group(new WorkGroup())
                .manager(new User())
                .build());
    }
}
