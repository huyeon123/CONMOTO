package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.entity.Board;
import com.huyeon.superspace.domain.board.entity.BoardStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BoardDto {
    private Long id;

    private String author;

    private String title;

    private String description;

    private String groupName;

    private String categoryName;

    private BoardStatus status;

    private List<TagDto> tags;

    private List<CommentDto> comments;

    private List<ContentDto> contents;

    private String url;

    private LocalDateTime updatedAt;

    @Builder
    public BoardDto(Board board) {
        id = board.getId();
        author = board.getUserEmail();
        title = board.getTitle();
        description = board.getDescription();
        groupName = board.getGroupName();
        status = board.getStatus();
        updatedAt = board.getUpdatedAt();
        url = "/workspace/" + board.getGroupUrl() + "/board/" + id;

        if (board.getCategory() != null) {
            categoryName = board.getCategoryName();
        }
    }
}
