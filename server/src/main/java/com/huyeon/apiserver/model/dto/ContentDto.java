package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.ContentBlock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentDto {
    private Long contentId;
    private String content;

    public ContentDto(ContentBlock content) {
        this.contentId = content.getId();
        this.content = content.getContent();
    }
}
