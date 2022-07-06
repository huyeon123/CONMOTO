package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.history.BoardHistory;
import com.huyeon.apiserver.repository.BoardRepository;
import com.huyeon.apiserver.repository.history.BoardHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.huyeon.apiserver.support.JsonParse.readJson;

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

    //게시글 작성
    public boolean writeBoard(String jsonBoard) {
        Board board = readJson(jsonBoard, Board.class);
        if (board == null) return false;
        boardRepository.save(board);
        return true;
    }

    //게시글 수정
    public boolean editBoard(Long id, String editBoard) {
        Optional<Board> optional = boardRepository.findById(id);
        Board current = optional.orElse(new Board());
        Board edit = readJson(editBoard, Board.class);
        if (edit != null
                && current.getId().equals(edit.getId())) {
            boardRepository.save(edit);
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
        if(board.isPresent()) {
            Optional<List<BoardHistory>> histories =
                    boardHistoryRepo.findAllByBoardId(board.get().getId());
            if (histories.isPresent()) return histories.get();
        }
        return List.of();
    }

}
