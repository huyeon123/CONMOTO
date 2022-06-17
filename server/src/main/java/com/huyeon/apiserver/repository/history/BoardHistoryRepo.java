package com.huyeon.apiserver.repository.history;

import com.huyeon.apiserver.model.dto.history.BoardHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardHistoryRepo extends JpaRepository<BoardHistory, Long> {
}
