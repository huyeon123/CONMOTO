package com.huyeon.apiserver.model.listener;

import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.history.BoardHistory;
import com.huyeon.apiserver.repository.history.BoardHistoryRepo;
import com.huyeon.apiserver.support.BeanUtils;

import javax.persistence.PreUpdate;

public class BoardHistoryListener implements HistoryListener{
    @PreUpdate
    @Override
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
