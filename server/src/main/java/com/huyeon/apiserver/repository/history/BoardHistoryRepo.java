package com.huyeon.apiserver.repository.history;

import com.huyeon.apiserver.model.dto.Board;
import com.huyeon.apiserver.model.dto.history.BoardHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardHistoryRepo extends JpaRepository<BoardHistory, Long> {
    List<BoardHistory> findAllByBoard(Board board);
}
