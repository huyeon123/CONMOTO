package com.huyeon.apiserver.support;

import com.huyeon.apiserver.model.entity.Noty;
import com.huyeon.apiserver.service.NotyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotyListener {
    private final NotyService notyService;

    @Async
    @TransactionalEventListener
    public void handleNoty(Noty noty) {
        notyService.publish(noty);
    }
}
