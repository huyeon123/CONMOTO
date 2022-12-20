package com.huyeon.superspace.global.model;

import com.huyeon.superspace.global.dto.NotyEventDto;
import org.springframework.context.ApplicationEventPublisher;

public class EventPublisher {

    public static void publishEvent(ApplicationEventPublisher eventPublisher, NotyEventDto noty) {
        eventPublisher.publishEvent(noty);
    }
}
