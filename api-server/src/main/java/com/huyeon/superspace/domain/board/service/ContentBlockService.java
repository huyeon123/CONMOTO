package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.dto.ContentDto;
import com.huyeon.superspace.domain.board.entity.Board;
import com.huyeon.superspace.domain.board.entity.ContentBlock;
import com.huyeon.superspace.domain.board.entity.history.ContentBlockHistory;
import com.huyeon.superspace.domain.board.repository.BoardRepository;
import com.huyeon.superspace.domain.board.repository.ContentBlockRepository;
import com.huyeon.superspace.domain.board.repository.history.ContentBlockHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentBlockService {
    private final BoardRepository boardRepository;
    private final ContentBlockRepository blockRepository;
    private final ContentBlockHistoryRepo blockHistoryRepo;

    public Long createContent(Long boardId) {
        Board board = findBoardById(boardId);

        ContentBlock block = ContentBlock.builder()
                .board(board)
                .build();

        return blockRepository.save(block).getId();
    }

    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow();
    }

    public void writeContents(Long contentId, ContentDto request) {
        ContentBlock current = blockRepository.findById(contentId).orElseThrow();
        current.setContent(request.getContent());
        blockRepository.save(current);
    }

    public void removeContent(Long id) {
        ContentBlock content = blockRepository.findById(id).orElseThrow();
        blockRepository.delete(content);
    }

    private List<ContentBlock> getContentBlockByBoardId(Long boardId) {
        return blockRepository.findAllByBoardId(boardId);
    }

    public List<ContentDto> getSummaryContentResByBoardId(Long boardId) {
        List<ContentBlock> contents = blockRepository.findTop3ByBoardId(boardId);
        return mapToContentDto(contents);
    }

    public List<ContentDto> getContentResponseByBoardId(Long boardId) {
        List<ContentBlock> contents = getContentBlockByBoardId(boardId);
        return mapToContentDto(contents);
    }

    private List<ContentDto> mapToContentDto(List<ContentBlock> contents) {
        return contents.stream()
                .map(ContentDto::new)
                .collect(Collectors.toList());
    }

    //컨텐츠 수정이력
    public List<ContentBlockHistory> contentHistory(Long contentId) {
        return blockHistoryRepo.findAllByBlockId(contentId);
    }
}
