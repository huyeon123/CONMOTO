package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.Board;
import com.huyeon.apiserver.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public List<Board> getAll() {
        return boardRepository.findAll();
    }

    public void saveAll(Board[] data) {
        boardRepository.saveAll(Arrays.asList(data));
    }

    public void deleteAll() {
        boardRepository.deleteAll();
    }

    public Board getByRandom(Long randomNum) {
        Optional<Board> board = boardRepository.findById(randomNum);
        return board.orElse(null); //isPresent()이면 board 리턴, 아니면 null 리턴
    }

    public void updateById(Board[] data) {
        boardRepository.saveAll(Arrays.asList(data));
    }

}
