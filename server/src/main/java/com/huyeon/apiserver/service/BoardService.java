package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.BoardReqDto;
import com.huyeon.apiserver.model.dto.BoardResDto;
import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.BoardStatus;
import com.huyeon.apiserver.model.entity.Category;
import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.model.entity.history.BoardHistory;
import com.huyeon.apiserver.repository.BoardRepository;
import com.huyeon.apiserver.repository.CategoryRepository;
import com.huyeon.apiserver.repository.GroupRepository;
import com.huyeon.apiserver.repository.history.BoardHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private static final int FIRST_LOAD = 0;

    private final GroupRepository groupRepository;
    private final CategoryRepository categoryRepository;
    private final BoardRepository boardRepository;
    private final BoardHistoryRepo boardHistoryRepo;

    //게시글 가져오기
    public Board getBoard(Long id) {
        return boardRepository.findById(id).orElseThrow();
    }

    private List<Board> getBoardsByCategory(Category category) {
        return boardRepository.findAllByCategory(category).orElse(List.of());
    }

    public BoardResDto getBoardResponse(Long id) {
        Board board = getBoard(id);
        return mapToBoardResDto(board);
    }

    public List<BoardResDto> getBoardResponsesByCategory(Category category) {
        List<Board> boards = getBoardsByCategory(category);
        return mapToDtoList(boards);
    }

    public List<BoardResDto> getNext10LatestInGroup(String groupUrl, Long lastId) throws Exception{
        Groups group = getGroupByUrl(groupUrl);
        List<Board> newest;

        if (lastId == FIRST_LOAD) {
            newest = boardRepository.find10Latest(group, PageRequest.of(0, 10));
        } else {
            newest = boardRepository.findNext10LatestInGroup(group, lastId, PageRequest.of(0, 5));
        }

        return mapToDtoList(newest);
    }

    public List<BoardResDto> getNext10LatestInCategory(String categoryName, Long lastId) {
        Category category = getCategoryByName(categoryName);
        List<Board> next10Latest = boardRepository.findNext10LatestInCategory(category, lastId, PageRequest.of(0, 1));
        return mapToDtoList(next10Latest);
    }

    private List<BoardResDto> mapToDtoList(List<Board> boards) {
        return boards.stream()
                .map(this::mapToBoardResDto)
                .collect(Collectors.toList());
    }

    private BoardResDto mapToBoardResDto(Board board) {
        BoardResDto boardResDto = BoardResDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .description(board.getDescription())
                .status(board.getStatus())
                .build();

        if (board.getCategory() != null) {
            boardResDto.setCategoryName(board.getCategory().getName());
        }

        return boardResDto;
    }

    //게시글 생성
    public Long createBoard(String email, String groupUrl) {
        Groups group = getGroupByUrl(groupUrl);

        Board board = Board.builder()
                .userEmail(email)
                .group(group)
                .title("")
                .status(BoardStatus.READY)
                .build();

        return boardRepository.save(board).getId();
    }

    //게시글 수정
    public void editBoard(BoardReqDto request) throws Exception {
        Board board = getBoard(request.getId());

        switch (request.getTarget()) {
            case "title":
                board.setTitle(request.getTitle());
                break;
            case "description":
                board.setDescription(request.getDescription());
                break;
            case "category":
                Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow();
                board.setCategory(category);
                break;
            case "status":
                board.setStatus(request.getStatus());
                break;
            default:
                throw new NoSuchMethodException();
        }

        boardRepository.save(board);
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

    private Groups getGroupByUrl(String groupUrl) throws NoSuchElementException{
        return groupRepository.findByUrlPath(groupUrl).orElseThrow();
    }

    private Category getCategoryByName(String categoryName) {
        return categoryRepository.findByName(categoryName).orElseThrow();
    }
}
