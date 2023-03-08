package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.document.Content;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Setter
@Getter
@NoArgsConstructor
public class ContentDto {
    private String id;

    private String html;

    private String lastUpdate;

    public ContentDto(Content content) {
        id = content.getId();
        html = content.getHtml();
        lastUpdate = content.getUpdatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
