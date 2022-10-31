package com.huyeon.superspace.domain.noty.dto;

import com.huyeon.superspace.domain.noty.entity.Noty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class EmitterAdaptorTest {
    @Test
    @DisplayName("EmitterAdaptor 생성")
    void createEmitterAdaptor() {
        //given
        String email = "test@test.com";

        //when, then
        assertDoesNotThrow(() -> new EmitterAdaptor());
        assertDoesNotThrow(
                () -> new EmitterAdaptor(
                        new SseEmitter(),
                        email,
                        email + System.currentTimeMillis(),
                        email + System.currentTimeMillis(),
                        "",
                        new Noty())
        );
        assertDoesNotThrow(
                () -> EmitterAdaptor.builder()
                        .emitter(new SseEmitter())
                        .emitterId(email + System.currentTimeMillis())
                        .eventId(email + System.currentTimeMillis())
                        .lastEventId("")
                        .data(new Noty())
                        .build()
        );
    }
}
