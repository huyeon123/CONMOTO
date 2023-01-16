package com.huyeon.superspace.domain.board.document;

import com.huyeon.superspace.domain.board.dto.ContentDto;
import com.huyeon.superspace.global.model.DocumentAudit;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "contents")
public class Content extends DocumentAudit {
    @Id
    private String id;
    private String markdown;

    @Builder
    public Content(ContentDto dto) {
        id = dto.getId();
        markdown = dto.getMarkdown();
    }
}
