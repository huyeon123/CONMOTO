package com.huyeon.apiserver.repository.history;

import com.huyeon.apiserver.model.entity.history.CommentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentHistoryRepo extends JpaRepository<CommentHistory, Long> {
    List<CommentHistory> findAllByCommentId(Long commentId);
}
