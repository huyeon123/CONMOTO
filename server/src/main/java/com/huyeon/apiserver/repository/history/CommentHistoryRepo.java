package com.huyeon.apiserver.repository.history;

import com.huyeon.apiserver.model.dto.history.CommentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentHistoryRepo extends JpaRepository<CommentHistory, Long> {
    Optional<List<CommentHistory>> findAllByCommentId(Long commentId);
}
