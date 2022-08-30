package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.ContentDto;
import com.huyeon.apiserver.model.entity.ContentBlock;
import com.huyeon.apiserver.model.entity.history.ContentBlockHistory;
import com.huyeon.apiserver.repository.ContentBlockRepository;
import com.huyeon.apiserver.repository.history.ContentBlockHistoryRepo;
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
    private final ContentBlockRepository blockRepository;
    private final ContentBlockHistoryRepo blockHistoryRepo;

    //컨텐츠 블럭 가져오기
    public ContentBlock getContentBlock(Long id) {
        return blockRepository.findById(id).orElse(new ContentBlock());
    }

    //게시글 컨텐츠 모두 가져오기
    public List<ContentBlock> getContentBlockByBoardId(Long boardId) {
        return blockRepository.findAllByBoardId(boardId);
    }

    public List<ContentDto> getSummaryContentResByBoardId(Long boardId){
        List<ContentBlock> contents = blockRepository.findTop3ByBoardId(boardId);
        return mapToContentDto(contents);
    }

    public List<ContentDto> getContentResponseByBoardId(Long boardId) {
        List<ContentBlock> contents = getContentBlockByBoardId(boardId);
        return mapToContentDto(contents);
    }

    private List<ContentDto> mapToContentDto(List<ContentBlock> contents) {
        return contents.stream()
                .map(content ->
                        new ContentDto(content.getId(), content.getContent())
                )
                .collect(Collectors.toList());
    }

    //컨텐츠 추가
    public Long createContent(Long boardId) {
        ContentBlock block = ContentBlock.builder()
                .boardId(boardId)
                .build();
        return blockRepository.save(block).getId();
    }

    //컨텐츠 작성
    public boolean writeContents(Long contentId, ContentBlock request) {
        ContentBlock current = blockRepository.findById(contentId).orElseThrow();
        current.setContent(request.getContent());
        blockRepository.save(current);
        return true;
    }

    //컨텐츠 삭제
    public boolean removeContent(Long id) {
        Optional<ContentBlock> optional = blockRepository.findById(id);
        if (optional.isPresent()) {
            blockRepository.delete(optional.get());
            return true;
        }
        return false;
    }

    //컨텐츠 수정이력
    public List<ContentBlockHistory> contentHistory(Long id) {
        Optional<ContentBlock> block = blockRepository.findById(id);
        if (block.isPresent()) {
            Optional<List<ContentBlockHistory>> histories =
                    blockHistoryRepo.findAllByBlockId(block.get().getId());
            if (histories.isPresent()) return histories.get();
        }
        return List.of();
    }
}
