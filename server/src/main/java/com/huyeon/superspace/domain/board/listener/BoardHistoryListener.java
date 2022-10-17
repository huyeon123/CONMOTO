package com.huyeon.superspace.domain.board.listener;

import com.huyeon.superspace.domain.board.entity.Board;
import com.huyeon.superspace.domain.board.entity.history.BoardHistory;
import com.huyeon.superspace.domain.board.repository.history.BoardHistoryRepo;
import com.huyeon.superspace.global.model.HistoryListener;
import com.huyeon.superspace.global.support.BeanUtils;

import javax.persistence.PreUpdate;

public class BoardHistoryListener implements HistoryListener {
    @PreUpdate
    public void loggingHistory(Object history) {
        if (history instanceof Board) {
            BoardHistoryRepo historyRepository = BeanUtils.getBean(BoardHistoryRepo.class);

            Board board = (Board) history;
            BoardHistory boardHistory = BoardHistory.builder()
                    .boardId(board.getId())
                    .pastTitle(board.getTitle())
                    .pastSTATUS(board.getStatus())
                    .build();

            boardHistory.setCreatedAt(board.getCreatedAt());

            historyRepository.save(boardHistory);
        }
    }

}
