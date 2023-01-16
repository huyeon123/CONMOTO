package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.document.Board;
import com.huyeon.superspace.domain.board.document.Content;
import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.ContentDto;
import com.huyeon.superspace.domain.board.repository.NewBoardRepository;
import com.huyeon.superspace.domain.board.repository.NewCommentRepository;
import com.huyeon.superspace.domain.board.repository.NewContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewBoardService {
    private final NewBoardRepository boardRepository;
    private final NewContentRepository contentRepository;
    private final NewCommentRepository commentRepository;

    public BoardDto getBoard(String id) {
        Optional<Board> optional = boardRepository.findById(id);
        return optional.map(BoardDto::new).orElseThrow();
    }

    public String createBoard(String groupUrl, String userEmail) {
        Content content = contentRepository.save(new Content());
        Board board = Board.builder()
                .author(userEmail)
                .groupUrl(groupUrl)
                .status("READY")
                .content(content)
                .build();

        return boardRepository.save(board).getId();
    }

    public String saveBoard(BoardDto request, String target) {
        BoardDto origin = getBoard(request.getId());
        change(origin, request, target);
        return boardRepository.save(new Board(origin)).getId();
    }

    private void change(BoardDto origin, BoardDto request, String target) {
        switch (target) {
            case "TITLE":
                origin.setTitle(request.getTitle());
                break;
            case "DESCRIPTION":
                origin.setDescription(request.getDescription());
                break;
            case "CATEGORY":
                origin.setCategoryName(request.getCategoryName());
                break;
            case "STATUS":
                origin.setStatus(request.getStatus());
                break;
            case "TAGS":
                origin.setTags(request.getTags());
                break;
        }
    }

    public void deleteBoard(String id) {
        Board board = boardRepository.findById(id).orElseThrow();
        contentRepository.delete(board.getContent());
        commentRepository.deleteAllByBoardId(board.getId());
        boardRepository.deleteById(id);
    }

    public ContentDto getContentById(String contentId) {
        Optional<Content> optional = contentRepository.findById(contentId);
        return optional.map(ContentDto::new).orElseThrow();
    }

    public String saveContent(ContentDto request) {
        return contentRepository.save(new Content(request)).getId();
    }

    public List<BoardDto> getNext10LatestInGroup(String groupUrl, int page) {
        List<Board> next = boardRepository.findNextLatestInGroup(
                groupUrl,
                LocalDateTime.now(),
                PageRequest.of(page, 10)
        );

        return mapToDtoList(next);
    }

    private List<BoardDto> mapToDtoList(List<Board> boards) {
        return boards.stream()
                .map(BoardDto::new)
                .collect(Collectors.toList());
    }

    public List<BoardDto> getNext10LatestInCategory(String categoryName, int page) {
        List<Board> next = boardRepository.findNextLatestInCategory(
                categoryName,
                LocalDateTime.now(),
                PageRequest.of(page, 10)
        );

        return mapToDtoList(next);
    }

    public List<BoardDto> getNext10LatestInUser(String userEmail, int page) {
        List<Board> next = boardRepository.findNextLatestInUser(
                userEmail,
                LocalDateTime.now(),
                PageRequest.of(page, 10)
        );

        return mapToDtoList(next);
    }

    public void deleteAllByGroupUrl(String groupUrl) {
        boardRepository.deleteAllByGroupUrl(groupUrl);
    }
}
