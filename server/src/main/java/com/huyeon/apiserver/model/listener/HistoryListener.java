package com.huyeon.apiserver.model.listener;

import com.huyeon.apiserver.model.Board;
import com.huyeon.apiserver.model.BoardHistory;
import com.huyeon.apiserver.repository.BoardHistoryRepository;
import com.huyeon.apiserver.support.BeanUtils;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class HistoryListener {
    @PrePersist
    @PreUpdate
    public void prePersistAndPreUpdate(Object o) {
        //@Autowired를 할 수 없어 빈을 직접 주입받아 사용
        BoardHistoryRepository historyRepository = BeanUtils.getBean(BoardHistoryRepository.class);

        Board board = (Board) o;
        BoardHistory boardHistory = BoardHistory.builder()
                .boardId(board.getId())
                .userId(board.getUserId())
                .title(board.getTitle())
                .content(board.getContent())
                .build();

        historyRepository.save(boardHistory);
    }
}