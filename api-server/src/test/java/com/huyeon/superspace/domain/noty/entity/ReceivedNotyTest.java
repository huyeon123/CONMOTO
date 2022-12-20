package com.huyeon.superspace.domain.noty.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ReceivedNotyTest {

    @Test
    @DisplayName("수신 알림 생성")
    void createReceivedNoty() {
        //given
        Noty noty = createTestNoty();

        //when, then
        assertDoesNotThrow(() -> ReceivedNoty.builder()
                .userEmail("email")
                .noty(noty)
                .build());
    }

    private Noty createTestNoty() {
        return Noty.builder()
                .senderName("email or groupName")
                .message("message")
                .type(NotyType.DIRECT_MESSAGE)
                .redirectUrl("/")
                .build();
    }
}
