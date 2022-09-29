package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.TagDto;
import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.Tag;
import com.huyeon.apiserver.repository.BoardRepository;
import com.huyeon.apiserver.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.patterns.PerFromSuper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final BoardRepository boardRepository;

    public List<TagDto> getTagResponseByBoardId(Long boardId) {
        List<Tag> tags = getTagsByBoardId(boardId);
        return mapToTagDto(tags);
    }

    private List<Tag> getTagsByBoardId(Long boardId) {
        return tagRepository.findAllByBoardId(boardId).orElse(List.of());
    }

    private List<TagDto> mapToTagDto(List<Tag> tags) {
        return tags.stream()
                .map(tag -> new TagDto(tag.getTag()))
                .collect(Collectors.toList());
    }

    //TODO: ChangeTag로 캡슐화 진행
    public void editTag(Long boardId, List<TagDto> request){
        Board board = boardRepository.findById(boardId).orElseThrow();
        List<Tag> original = getTagsByBoardId(boardId);

        int originSize = original.size();
        int changeSize = request.size();
        int overWriteNum = Math.min(originSize, changeSize);

        for (int i = 0; i < overWriteNum; i++) {
            overWriteTag(original.get(i), request.get(i));
        }

        if (original.size() > request.size()) {
            for (int i = overWriteNum; i < original.size(); i++) {
                tagRepository.delete(original.get(i));
            }
        } else if (original.size() < request.size()) {
            for (int i = overWriteNum; i < request.size(); i++) {
                Tag tag = Tag.builder().board(board)
                        .tag(request.get(i).getTag())
                        .build();
                tagRepository.save(tag);
            }
        }
    }

    private void overWriteTag(Tag origin, TagDto change) {
        origin.setTag(change.getTag());
        tagRepository.save(origin);
    }
}
