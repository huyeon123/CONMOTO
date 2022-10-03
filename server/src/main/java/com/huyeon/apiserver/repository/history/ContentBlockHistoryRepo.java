package com.huyeon.apiserver.repository.history;

import com.huyeon.apiserver.model.entity.history.ContentBlockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentBlockHistoryRepo extends JpaRepository<ContentBlockHistory, Long> {
    List<ContentBlockHistory> findAllByBlockId(Long blockId);
}
