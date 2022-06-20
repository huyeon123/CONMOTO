package com.huyeon.apiserver.repository.history;

import com.huyeon.apiserver.model.dto.Comment;
import com.huyeon.apiserver.model.dto.history.CommentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentHistoryRepo extends JpaRepository<CommentHistory, Long> {
    List<CommentHistory> findAllByComment(Comment comment);
}
