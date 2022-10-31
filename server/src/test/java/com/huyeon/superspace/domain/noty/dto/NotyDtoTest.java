package com.huyeon.superspace.domain.noty.dto;

import com.huyeon.superspace.domain.noty.entity.Noty;
import com.huyeon.superspace.domain.noty.entity.NotyType;
import com.huyeon.superspace.domain.noty.entity.ReceivedNoty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class NotyDtoTest {
    @Test
    @DisplayName("NotyDto 생성")
    void createNotyDto(){
        //given
        Noty noty = Noty.builder()
                .senderName("email or groupName")
                .message("message")
                .type(NotyType.DIRECT_MESSAGE)
                .redirectUrl("/")
                .build();

        ReceivedNoty receivedNoty = ReceivedNoty.builder()
                .userEmail("email")
                .noty(noty)
                .build();

        //when
        assertDoesNotThrow(() -> new NotyDto(receivedNoty));
    }
}
