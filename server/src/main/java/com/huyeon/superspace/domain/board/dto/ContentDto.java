package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.entity.ContentBlock;
import lombok.*;

@Getter
@NoArgsConstructor
public class ContentDto {
    private Long contentId;
    private String content;

    public ContentDto(String content) {
        this.content = content;
    }

    @Builder
    public ContentDto(ContentBlock content) {
        this.contentId = content.getId();
        this.content = content.getContent();
    }
}
