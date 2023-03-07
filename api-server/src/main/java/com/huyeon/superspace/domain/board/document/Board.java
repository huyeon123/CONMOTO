package com.huyeon.superspace.domain.board.document;

import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.global.model.DocumentAudit;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "boards")
public class Board extends DocumentAudit {
    @Transient
    public static final String SEQUENCE_NAME = "board_sequence";

    @Id
    private Long id;

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
