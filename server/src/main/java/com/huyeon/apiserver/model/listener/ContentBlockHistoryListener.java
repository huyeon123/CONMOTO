package com.huyeon.apiserver.model.listener;

import com.huyeon.apiserver.model.entity.ContentBlock;
import com.huyeon.apiserver.model.entity.history.ContentBlockHistory;
import com.huyeon.apiserver.repository.history.ContentBlockHistoryRepo;
import com.huyeon.apiserver.support.BeanUtils;

import javax.persistence.PreUpdate;

public class ContentBlockHistoryListener implements HistoryListener{
    @PreUpdate
    @Override
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
