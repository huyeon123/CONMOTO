package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.BoardHeaderReqDto;
import com.huyeon.apiserver.model.dto.BoardReqDto;
import com.huyeon.apiserver.model.dto.BoardResDto;
import com.huyeon.apiserver.model.dto.ContentDto;
import com.huyeon.apiserver.model.entity.*;
import com.huyeon.apiserver.model.entity.history.BoardHistory;
import com.huyeon.apiserver.repository.BoardRepository;
import com.huyeon.apiserver.repository.CategoryRepository;
import com.huyeon.apiserver.repository.ContentBlockRepository;
import com.huyeon.apiserver.repository.GroupRepository;
import com.huyeon.apiserver.repository.history.BoardHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final GroupRepository groupRepository;
    private final CategoryRepository categoryRepository;
    private final BoardRepository boardRepository;
    private final ContentBlockRepository blockRepository;
    private final BoardHistoryRepo boardHistoryRepo;

    //게시글 가져오기
    public Board getBoard(Long id) {
        return boardRepository.findById(id).orElseThrow();
    }

    private List<Board> getBoardsByCategory(Category category) {
        return boardRepository.findAllByCategory(category);
    }

    public BoardResDto getBoardResponse(Long id) {
        Board board = getBoard(id);
        return new BoardResDto(board);
    }

    public List<BoardResDto> getBoardResponsesByCategory(Category category) {
        List<Board> boards = getBoardsByCategory(category);
        return mapToDtoList(boards);
    }

    private List<BoardResDto> mapToDtoList(List<Board> boards) {
        return boards.stream()
                .map(BoardResDto::new)
                .collect(Collectors.toList());
    }

    //Template Method Pattern 적용하기
    public List<BoardResDto> getNext10LatestInGroup(BoardReqDto request) {
        Groups group = getGroupByUrl(request.getQuery());
        List<Board> newest = boardRepository.findNextTenLatestInGroup(group, request.getNow(), PageRequest.of(request.getNextPage(), 10));
        return mapToDtoList(newest);
    }

    public List<BoardResDto> getNext10LatestInCategory(BoardReqDto request) {
        Category category = getCategoryByName(request.getQuery());
        List<Board> newest = boardRepository.findNextTenLatestInCategory(category, request.getNow(), PageRequest.of(request.getNextPage(), 10));
        List<BoardResDto> boardDtoList = mapToDtoList(newest);
        addContents(boardDtoList);
        return boardDtoList;
    }

    private void addContents(List<BoardResDto> newest) {
        newest.forEach(board -> {
            List<ContentDto> summaryContents = blockRepository.findTop3ByBoardId(board.getId())
                    .stream()
                    .map(ContentDto::new)
                    .collect(Collectors.toList());

            board.setContents(summaryContents);
        });
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

        Long id = boardRepository.save(board).getId();

        createDefaultContent(id);

        return id;
    }

    private void createDefaultContent(Long boardId) {
        blockRepository.save(new ContentBlock(boardId));
    }

    //게시글 수정
    public void editBoard(BoardHeaderReqDto request) throws Exception {
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
