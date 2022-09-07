package com.huyeon.apiserver.model;

import com.huyeon.apiserver.model.dto.NotyEventDto;
import com.huyeon.apiserver.model.entity.Noty;
import org.springframework.context.ApplicationEventPublisher;

public class MyEvent {

    public static void publishEvent(ApplicationEventPublisher eventPublisher, NotyEventDto noty) {
        eventPublisher.publishEvent(noty);
    }
}
