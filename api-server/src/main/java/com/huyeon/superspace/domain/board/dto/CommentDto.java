package com.huyeon.superspace.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huyeon.superspace.domain.board.document.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {
    private Long id;

    private String groupUrl;
    private Long boardId;
    private String author;
    private String body;
    private String tag;

    private boolean mine;

    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime lastUpdate;

    public CommentDto(Comment comment, String nickname, boolean mine) {
        id = comment.getId();
        groupUrl = comment.getGroupUrl();
        boardId = comment.getBoardId();
        author = nickname;
        body = comment.getBody();
        tag = comment.getTag();
        lastUpdate = comment.getUpdatedAt();
        this.mine = mine;
    }

    public CommentDto(Comment comment) {
        id = comment.getId();
        groupUrl = comment.getGroupUrl();
        boardId = comment.getBoardId();
        author = comment.getAuthor();
        body = comment.getBody();
        tag = comment.getTag();
        lastUpdate = comment.getUpdatedAt();
    }
}
