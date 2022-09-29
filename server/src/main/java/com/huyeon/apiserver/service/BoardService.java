package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.BoardHeaderReqDto;
import com.huyeon.apiserver.model.dto.BoardResDto;
import com.huyeon.apiserver.model.dto.ContentDto;
import com.huyeon.apiserver.model.dto.PageReqDto;
import com.huyeon.apiserver.model.entity.*;
import com.huyeon.apiserver.model.entity.history.BoardHistory;
import com.huyeon.apiserver.repository.*;
import com.huyeon.apiserver.repository.history.BoardHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
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
    private final ContentBlockRepository contentRepository;
    private final BoardHistoryRepo boardHistoryRepo;

    //게시글 가져오기
    public Optional<Board> getBoard(Long id) {
        return boardRepository.findById(id);
    }

    private List<Board> getBoardsByCategory(Category category) {
        return boardRepository.findAllByCategory(category);
    }

    public BoardResDto getBoardResponse(Long id) {
        Board board = getBoard(id).orElseThrow();
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

    public List<BoardResDto> getNext10LatestInGroup(PageReqDto request) {
        Optional<WorkGroup> group = getGroupByUrl(request.getQuery());

        if (group.isEmpty()) {
            return List.of();
        }

        List<Board> newest = boardRepository.findNextTenLatest(group.get(), request.getNow(), PageRequest.of(request.getNextPage(), 10));
        List<BoardResDto> boardDtoList = mapToDtoList(newest);

        addContents(boardDtoList);
        return boardDtoList;
    }

    public List<BoardResDto> getNext10LatestInCategory(PageReqDto request) {
        Category category = getCategoryByName(request.getQuery());

        List<Board> newest = boardRepository.findNextTenLatest(category, request.getNow(), PageRequest.of(request.getNextPage(), 10));
        List<BoardResDto> boardDtoList = mapToDtoList(newest);

        addContents(boardDtoList);
        return boardDtoList;
    }

    private void addContents(List<BoardResDto> newest) {
        newest.forEach(board -> {
            List<ContentDto> summaryContents = contentRepository.findTop3ByBoardId(board.getId())
                    .stream()
                    .map(ContentDto::new)
                    .collect(Collectors.toList());

            board.setContents(summaryContents);
        });
    }

    public List<BoardResDto> getNext10LatestInUser(PageReqDto request, User user) {
        List<Board> newest = boardRepository.findNextLatest(user, request.getNow(), PageRequest.of(request.getNextPage(), 10));
        List<BoardResDto> boardResDtoList = mapToDtoList(newest);
        addContents(boardResDtoList);
        return boardResDtoList;
    }

    //게시글 생성
    public Long createBoard(String email, String groupUrl) {
        WorkGroup group = getGroupByUrl(groupUrl).orElseThrow();

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
        contentRepository.save(new ContentBlock(boardId));
    }

    //게시글 수정
    public void editBoard(BoardHeaderReqDto request){
        Board board = getBoard(request.getId()).orElseThrow();

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
        Optional<Board> board = getBoard(id);
        if (board.isPresent()) {
            return boardHistoryRepo.findAllByBoardId(board.get().getId());
        }
        return List.of();
    }

    private Optional<WorkGroup> getGroupByUrl(String groupUrl){
        return groupRepository.findByUrlPath(groupUrl);
    }

    private Category getCategoryByName(String categoryName) {
        return categoryRepository.findByName(categoryName).orElseThrow();
    }
}
