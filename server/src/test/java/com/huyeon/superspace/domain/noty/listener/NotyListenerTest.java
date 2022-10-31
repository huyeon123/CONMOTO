package com.huyeon.superspace.domain.noty.listener;

import com.huyeon.superspace.domain.noty.entity.Noty;
import com.huyeon.superspace.domain.noty.entity.NotyType;
import com.huyeon.superspace.domain.noty.entity.ReceivedNoty;
import com.huyeon.superspace.global.dto.NotyEventDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class NotyListenerTest {

    @Autowired
    NotyListener notyListener;

    @Test
    @DisplayName("Async TransactionalEventListener Test")
    void handleNoty() {
        //given
        Noty noty = Noty.builder()
                .senderName("sub@test.com")
                .message("Tagged!")
                .type(NotyType.BOARD_TAGGED)
                .build();

        List<ReceivedNoty> receivers = List.of(
                new ReceivedNoty(null, noty, "test@test.com", false),
                new ReceivedNoty(null, noty, "admin@test.com", false)
        );

        NotyEventDto eventDto = NotyEventDto.builder()
                .noty(noty)
                .receivers(receivers)
                .build();

        //when
        assertDoesNotThrow(() -> notyListener.handleNoty(eventDto));
    }
}
