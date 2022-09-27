package com.huyeon.apiserver.model.listener;

import com.huyeon.apiserver.model.dto.NotyEventDto;
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
    public void handleNoty(NotyEventDto noty) {
        notyService.publish(noty);
    }
}
