package com.huyeon.superspace.domain.noty.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class NotyTest {

    @Test
    @DisplayName("알림 생성")
    void createNoty(){
        assertDoesNotThrow(() -> Noty.builder()
                .senderName("email or groupName")
                .message("any message")
                .type(NotyType.NOTICE)
                .redirectUrl("/")
                .build());
    }
}
