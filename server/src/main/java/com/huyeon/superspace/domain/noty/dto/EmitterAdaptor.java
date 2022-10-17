package com.huyeon.superspace.domain.noty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmitterAdaptor {
    private SseEmitter emitter;
    private String userEmail;
    private String emitterId;
    private String eventId;
    private String lastEventId;
    private Object data;
}
