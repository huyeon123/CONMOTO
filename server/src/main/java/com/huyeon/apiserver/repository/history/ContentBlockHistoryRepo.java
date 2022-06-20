package com.huyeon.apiserver.repository.history;

import com.huyeon.apiserver.model.dto.ContentBlock;
import com.huyeon.apiserver.model.dto.history.ContentBlockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentBlockHistoryRepo extends JpaRepository<ContentBlockHistory, Long> {
    List<ContentBlockHistory> findAllByContentBlock(ContentBlock contentBlock);
}
