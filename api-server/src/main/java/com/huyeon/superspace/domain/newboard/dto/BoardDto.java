package com.huyeon.superspace.domain.newboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huyeon.superspace.domain.newboard.document.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class BoardDto {
    private String id;

    private String author;

    private String title;

    private String description;

    private String groupUrl;

    private String categoryName;

    private String status;

    private String[] tags;

    private ContentDto content;

    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime lastUpdate;

    public BoardDto(Board board) {
        id = board.getId();
        author = board.getAuthor();
        title = board.getTitle();
        description = board.getDescription();
        groupUrl = board.getGroupUrl();
        categoryName = board.getCategoryName();
        status = board.getStatus();
        tags = board.getTags();
        lastUpdate = board.getUpdatedAt();

        if (Objects.nonNull(board.getContent())) {
            content = new ContentDto(board.getContent());
        }
    }
}
