package com.huyeon.superspace.domain.board.repository.history;

import com.huyeon.superspace.domain.board.entity.history.CommentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentHistoryRepo extends JpaRepository<CommentHistory, Long> {
    List<CommentHistory> findAllByCommentId(Long commentId);
}
