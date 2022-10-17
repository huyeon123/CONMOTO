package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.entity.ContentBlock;
import lombok.*;

@Getter
public class ContentDto {
    private Long contentId;
    private String content;

    @Builder
    public ContentDto(ContentBlock content) {
        this.contentId = content.getId();
        this.content = content.getContent();
    }
}
