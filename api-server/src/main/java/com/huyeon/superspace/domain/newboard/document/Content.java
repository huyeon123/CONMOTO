package com.huyeon.superspace.domain.newboard.document;

import com.huyeon.superspace.domain.newboard.dto.ContentDto;
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
public class Content extends DocumentAudit{
    @Id
    private String id;
    private String markdown;

    @Builder
    public Content(ContentDto dto) {
        id = dto.getId();
        markdown = dto.getMarkdown();
    }
}
