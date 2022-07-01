package com.huyeon.apiserver.repository.history;

import com.huyeon.apiserver.model.dto.history.ContentBlockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentBlockHistoryRepo extends JpaRepository<ContentBlockHistory, Long> {
    Optional<List<ContentBlockHistory>> findAllByBlockId(Long blockId);
}
