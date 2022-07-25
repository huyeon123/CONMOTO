package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.entity.ContentBlock;
import com.huyeon.apiserver.model.entity.history.ContentBlockHistory;
import com.huyeon.apiserver.repository.ContentBlockRepository;
import com.huyeon.apiserver.repository.history.ContentBlockHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.huyeon.apiserver.support.JsonParse.readJson;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentBlockService {
    private final ContentBlockRepository blockRepository;
    private final ContentBlockHistoryRepo blockHistoryRepo;

    //블록 가져오기
    public ContentBlock getContentBlock(Long id) {
        return blockRepository.findById(id).orElse(new ContentBlock());
    }

    public List<ContentBlock> getContentBlockByBoardId(Long boardId) {
        return blockRepository.findAllByBoardId(boardId).orElse(List.of());
    }

    //블록 추가
    public boolean writeContent(String jsonContent) {
        ContentBlock block = readJson(jsonContent, ContentBlock.class);
        if(block == null) return false;
        blockRepository.save(block);
        return true;
    }

    public boolean writeContents(Long boardId, List<ContentBlock> request) {
        try {
            request.forEach(block -> block.setBoardId(boardId));
            blockRepository.saveAll(request);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //블록 수정
    public boolean editContent(Long id, String editComment) {
        Optional<ContentBlock> optional = blockRepository.findById(id);
        ContentBlock current = optional.orElse(new ContentBlock());

        ContentBlock edit = readJson(editComment, ContentBlock.class);

        if (edit != null
                && edit.getId().equals(current.getId())) {
            blockRepository.save(edit);
            return true;
        }
        return false;
    }


    //블록 삭제
    public boolean removeContent(Long id) {
        Optional<ContentBlock> optional = blockRepository.findById(id);
        if (optional.isPresent()) {
            blockRepository.delete(optional.get());
            return true;
        }
        return false;
    }

    //블록 수정이력
    public List<ContentBlockHistory> contentHistory(Long id) {
        Optional<ContentBlock> block = blockRepository.findById(id);
        if(block.isPresent()) {
            Optional<List<ContentBlockHistory>> histories =
                    blockHistoryRepo.findAllByBlockId(block.get().getId());
            if (histories.isPresent()) return histories.get();
        }
        return List.of();
    }
}
