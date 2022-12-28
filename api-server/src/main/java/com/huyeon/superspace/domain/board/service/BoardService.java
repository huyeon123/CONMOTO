package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.dto.BoardHeaderDto;
import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.ContentDto;
import com.huyeon.superspace.domain.board.dto.PageReqDto;
import com.huyeon.superspace.domain.board.entity.Board;
import com.huyeon.superspace.domain.board.entity.BoardStatus;
import com.huyeon.superspace.domain.board.entity.Category;
import com.huyeon.superspace.domain.board.entity.ContentBlock;
import com.huyeon.superspace.domain.board.repository.*;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.group.repository.GroupRepository;
import com.huyeon.superspace.domain.board.entity.history.BoardHistory;
import com.huyeon.superspace.domain.board.repository.history.BoardHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
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
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;
    private final BoardHistoryRepo boardHistoryRepo;

    //게시글 가져오기
    private Board getBoard(Long id) {
        return boardRepository.findById(id).orElseThrow();
    }

    public BoardDto getBoardDto(Long id) {
        Board board = getBoard(id);
        return new BoardDto(board);
    }

    public List<BoardDto> getBoardResponsesByCategory(Category category) {
        List<Board> boards = getBoardsByCategory(category);
        return mapToDtoList(boards);
    }

    private List<Board> getBoardsByCategory(Category category) {
        return boardRepository.findAllByCategory(category);
    }

    private List<BoardDto> mapToDtoList(List<Board> boards) {
        return boards.stream()
                .map(BoardDto::new)
                .collect(Collectors.toList());
    }

    public List<BoardDto> getNext10LatestInGroup(PageReqDto request) {
        Optional<WorkGroup> group = getGroupByUrl(request.getQuery());

        if (group.isEmpty()) {
            return List.of();
        }

        List<Board> newest = boardRepository.findNextTenLatest(group.get(), LocalDateTime.now(), PageRequest.of(request.getNextPage(), 10));
        List<BoardDto> boardDtoList = mapToDtoList(newest);

        addContents(boardDtoList);
        return boardDtoList;
    }

    public List<BoardDto> getNext10LatestInCategory(PageReqDto request) {
        Category category = getCategoryByName(request.getQuery());

        List<Board> newest = boardRepository.findNextTenLatest(category, LocalDateTime.now(), PageRequest.of(request.getNextPage(), 10));
        List<BoardDto> boardDtoList = mapToDtoList(newest);

        addContents(boardDtoList);
        return boardDtoList;
    }

    private void addContents(List<BoardDto> newest) {
        newest.forEach(board -> {
            List<ContentDto> summaryContents = contentRepository.findTop3ByBoardId(board.getId())
                    .stream()
                    .map(ContentDto::new)
                    .collect(Collectors.toList());

            board.setContents(summaryContents);
        });
    }

    public List<BoardDto> getNext10LatestInUser(PageReqDto request, String email) {
        List<Board> newest = boardRepository.findNextTenLatest(email, LocalDateTime.now(), PageRequest.of(request.getNextPage(), 10));
        List<BoardDto> boardDtoList = mapToDtoList(newest);
        addContents(boardDtoList);
        return boardDtoList;
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

        board = boardRepository.save(board);

        createDefaultContent(board);

        return board.getId();
    }

    private void createDefaultContent(Board board) {
        contentRepository.save(new ContentBlock(board));
    }

    //게시글 수정
    public void editBoard(BoardHeaderDto request) {
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
        }

        boardRepository.save(board);
    }

    //게시글 삭제
    public void removeBoard(Long id) {
        if (boardRepository.existsById(id)) {
            tagRepository.deleteAllByBoardId(id);
            commentRepository.deleteAllByBoardId(id);
            contentRepository.deleteAllByBoardId(id);
            boardRepository.deleteById(id);
        }
    }

    //게시글 수정이력
    public List<BoardHistory> findBoardHistory(Long id) {
        Board board = getBoard(id);
        return boardHistoryRepo.findAllByBoardId(board.getId());
    }

    private Optional<WorkGroup> getGroupByUrl(String groupUrl) {
        return groupRepository.findByUrlPath(groupUrl);
    }

    private Category getCategoryByName(String categoryName) {
        return categoryRepository.findByName(categoryName).orElseThrow();
    }
}
