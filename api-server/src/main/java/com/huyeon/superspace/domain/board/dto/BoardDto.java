package com.huyeon.superspace.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huyeon.superspace.domain.board.document.Board;
import com.huyeon.superspace.domain.board.dto.ContentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class BoardDto {
    private Long id;

    private String author;

    private String title;

    private String description;

    private String groupUrl;

    private String categoryId;

    private String categoryName;

    private String status;

    private String[] tags;

    private ContentDto content;

    private String lastUpdate;

    private String url;

    public BoardDto(Board board) {
        id = board.getId();
        author = board.getAuthor();
        title = board.getTitle();
        description = board.getDescription();
        groupUrl = board.getGroupUrl();
        categoryId = board.getCategoryId();
        categoryName = board.getCategoryName();
        status = board.getStatus();
        tags = board.getTags();
        lastUpdate = board.getUpdatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        url = "/community/" + groupUrl + "/board/" + id;

        if (Objects.nonNull(board.getContent())) {
            content = new ContentDto(board.getContent());
        }
    }
}
