package com.huyeon.apiserver.repository.history;

import com.huyeon.apiserver.model.dto.history.ContentBlockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentBlockHistoryRepo extends JpaRepository<ContentBlockHistory, Long> {
}
