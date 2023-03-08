package com.huyeon.superspace.domain.board.document;

import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.ContentDto;
import com.huyeon.superspace.global.model.DocumentAudit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "temp_post")
public class TempPost extends DocumentAudit {
    @Transient
    public static final String SEQUENCE_NAME = "temp_post_sequence";

    @Id
    private Long id;

    @Setter
    private String author;

    private String title;

    private String description;

    @Field("group_url")
    private String groupUrl;
    @Field("category_name")
    private String categoryName;

    private String status;

    private String[] tags;

    private TempContent content;

    public TempPost(BoardDto dto) {
        id = dto.getId();
        author = dto.getAuthor();
        title = dto.getTitle();
        description = dto.getDescription();
        groupUrl = dto.getGroupUrl();
        categoryName = dto.getCategoryName();
        status = dto.getStatus();
        tags = dto.getTags();

        if (Objects.nonNull(dto.getContent())) {
            content = new TempContent(dto.getContent());
        }
    }

    @Getter
    @NoArgsConstructor
    static class TempContent {
        private String markdown;

        public TempContent(ContentDto contentDto) {
            markdown = contentDto.getHtml();
        }
    }
}
