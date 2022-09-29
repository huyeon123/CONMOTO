package com.huyeon.apiserver.repository.history;

import com.huyeon.apiserver.model.entity.history.BoardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardHistoryRepo extends JpaRepository<BoardHistory, Long> {
    List<BoardHistory> findAllByBoardId(Long boardId);
}
