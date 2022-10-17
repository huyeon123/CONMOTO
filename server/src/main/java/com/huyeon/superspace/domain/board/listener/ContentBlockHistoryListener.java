package com.huyeon.superspace.domain.board.listener;

import com.huyeon.superspace.domain.board.entity.ContentBlock;
import com.huyeon.superspace.domain.board.entity.history.ContentBlockHistory;
import com.huyeon.superspace.domain.board.repository.history.ContentBlockHistoryRepo;
import com.huyeon.superspace.global.model.HistoryListener;
import com.huyeon.superspace.global.support.BeanUtils;

import javax.persistence.PreUpdate;

public class ContentBlockHistoryListener implements HistoryListener {
    @PreUpdate
    public void loggingHistory(Object history) {
        if (history instanceof ContentBlock) {
            ContentBlockHistoryRepo contentBlockHistoryRepo = BeanUtils.getBean(ContentBlockHistoryRepo.class);

            ContentBlock contentBlock = (ContentBlock) history;
            ContentBlockHistory contentBlockHistory = ContentBlockHistory.builder()
                    .blockId(contentBlock.getId())
                    .pastContent(contentBlock.getContent())
                    .build();

            contentBlockHistory.setCreatedAt(contentBlock.getCreatedAt());

            contentBlockHistoryRepo.save(contentBlockHistory);
        }
    }
}
