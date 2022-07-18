package com.huyeon.apiserver.model.listener;

import com.huyeon.apiserver.model.entity.Comment;
import com.huyeon.apiserver.model.entity.history.CommentHistory;
import com.huyeon.apiserver.repository.history.CommentHistoryRepo;
import com.huyeon.apiserver.support.BeanUtils;

import javax.persistence.PreUpdate;

public class CommentHistoryListener implements HistoryListener{
    @PreUpdate
    @Override
    public void loggingHistory(Object history) {
        if (history instanceof Comment) {
            CommentHistoryRepo commentHistoryRepo = BeanUtils.getBean(CommentHistoryRepo.class);

            Comment comment = (Comment) history;
            CommentHistory commentHistory = CommentHistory.builder()
                    .commentId(comment.getId())
                    .pastComment(comment.getComment())
                    .build();

            commentHistory.setCreatedAt(comment.getCreatedAt());

            commentHistoryRepo.save(commentHistory);
        }
    }
}
