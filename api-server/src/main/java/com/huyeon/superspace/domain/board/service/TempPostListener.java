package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.document.TempPost;
import com.huyeon.superspace.domain.board.service.AutoIncrementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TempPostListener extends AbstractMongoEventListener<TempPost> {
    private final AutoIncrementService autoIncrementService;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<TempPost> event) {
        if (Objects.isNull(event.getSource().getId())) {
            event.getSource().setId(autoIncrementService.getNextSequence(TempPost.SEQUENCE_NAME));
        }
    }
}
