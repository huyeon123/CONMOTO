package com.huyeon.apiserver.repository.history;

import com.huyeon.apiserver.model.dto.history.CommentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentHistoryRepo extends JpaRepository<CommentHistory, Long> {
}
