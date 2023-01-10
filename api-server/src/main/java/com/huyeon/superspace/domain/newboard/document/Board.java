package com.huyeon.superspace.domain.newboard.document;

import com.huyeon.superspace.domain.newboard.dto.BoardDto;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "boards")
public class Board extends DocumentAudit {
    @Id
    private String id;

    private String author;

    private String title;

    private String description;

    @Field("group_url")
    private String groupUrl;
    @Field("category_name")
    private String categoryName;

    private String status;

    private String[] tags;

    @DBRef
    private Content content;

    public Board(BoardDto dto) {
        id = dto.getId();
        author = dto.getAuthor();
        title = dto.getTitle();
        description = dto.getDescription();
        groupUrl = dto.getGroupUrl();
        categoryName = dto.getCategoryName();
        status = dto.getStatus();
        tags = dto.getTags();

        if (Objects.nonNull(dto.getContent())) {
            content = new Content(dto.getContent());
        }
    }
}
