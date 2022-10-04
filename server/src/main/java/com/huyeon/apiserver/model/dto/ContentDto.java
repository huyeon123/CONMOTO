package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.ContentBlock;
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
