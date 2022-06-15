package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.BoardHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardHistoryRepository extends JpaRepository<BoardHistory, Long> {
}
