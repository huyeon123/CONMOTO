package com.huyeon.superspace.domain.board.repository.history;

import com.huyeon.superspace.domain.board.entity.history.BoardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardHistoryRepo extends JpaRepository<BoardHistory, Long> {
    List<BoardHistory> findAllByBoardId(Long boardId);
}
