package com.huyeon.superspace.domain.noty.listener;

import com.huyeon.superspace.global.dto.NotyEventDto;
import com.huyeon.superspace.domain.noty.service.NotyService;
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
