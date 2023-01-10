package com.huyeon.superspace.domain.newboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huyeon.superspace.domain.newboard.document.Content;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ContentDto {
    private String id;

    private String markdown;

    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime lastUpdate;

    public ContentDto(Content content) {
        id = content.getId();
        markdown = content.getMarkdown();
        lastUpdate = content.getUpdatedAt();
    }
}
