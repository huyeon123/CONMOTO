package com.huyeon.apiserver.model;

import com.huyeon.apiserver.model.entity.Noty;
import org.springframework.context.ApplicationEventPublisher;

public class MyEvent {

    public static void publishEvent(ApplicationEventPublisher eventPublisher, Noty noty) {
        eventPublisher.publishEvent(noty);
    }
}
