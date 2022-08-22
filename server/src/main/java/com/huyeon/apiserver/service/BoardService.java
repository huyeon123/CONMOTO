package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.Category;
import com.huyeon.apiserver.model.entity.history.BoardHistory;
import com.huyeon.apiserver.repository.BoardRepository;
import com.huyeon.apiserver.repository.history.BoardHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardHistoryRepo boardHistoryRepo;

    //게시글 가져오기
    public Optional<Board> getBoard(Long id) {
        return boardRepository.findById(id);
    }

    public List<Board> getBoardsByCategory(Category category) {
        return boardRepository.findAllByCategory(category).orElse(List.of());
    }

    //게시글 생성
    public Long createBoard(Board board) {
        return boardRepository.save(board).getId();
    }

    //게시글 수정
    public boolean editBoard(String email, Board editBoard) {
        Board current = boardRepository.findById(editBoard.getId()).orElse(new Board());
        if (current.getUserEmail().equals(email)
                && current.getId().equals(editBoard.getId())) {
            editBoard.setUserEmail(email);
            boardRepository.save(editBoard);
            return true;
        }
        return false;
    }

    //게시글 삭제
    public boolean removeBoard(Long id) {
        Optional<Board> optional = boardRepository.findById(id);
        if (optional.isPresent()) {
            boardRepository.delete(optional.get());
            return true;
        }
        return false;
    }

    //게시글 수정이력
    public List<BoardHistory> boardHistory(Long id) {
        Optional<Board> board = boardRepository.findById(id);
        if (board.isPresent()) {
            Optional<List<BoardHistory>> histories =
                    boardHistoryRepo.findAllByBoardId(board.get().getId());
            if (histories.isPresent()) return histories.get();
        }
        return List.of();
    }

}
