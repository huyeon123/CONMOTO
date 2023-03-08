package com.huyeon.superspace.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huyeon.superspace.domain.board.document.Board;
import com.huyeon.superspace.domain.board.document.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentPreviewDto {
    private String boardTitle;

    private String body;

    private String tag;

    private String url;

    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime lastUpdate;

    public CommentPreviewDto(Comment comment, Board board) {
        boardTitle = board.getTitle();
        body = comment.getBody();
        tag = comment.getTag();
        url = "/community/" + board.getGroupUrl() + "/board/" + board.getId();
        lastUpdate = comment.getUpdatedAt();
    }
}
