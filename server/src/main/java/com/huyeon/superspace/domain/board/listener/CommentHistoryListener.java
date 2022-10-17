package com.huyeon.superspace.domain.board.listener;

import com.huyeon.superspace.domain.board.entity.Comment;
import com.huyeon.superspace.domain.board.entity.history.CommentHistory;
import com.huyeon.superspace.domain.board.repository.history.CommentHistoryRepo;
import com.huyeon.superspace.global.model.HistoryListener;
import com.huyeon.superspace.global.support.BeanUtils;

import javax.persistence.PreUpdate;

public class CommentHistoryListener implements HistoryListener {
    @PreUpdate
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
