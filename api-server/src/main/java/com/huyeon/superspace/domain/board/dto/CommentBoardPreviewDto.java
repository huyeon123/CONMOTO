package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.document.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class CommentBoardPreviewDto {
    private Long id;
    private Long boardId;
    private String title;
    private String description;
    private String author;
    private String lastUpdate;
    private String url;

    public CommentBoardPreviewDto(Long commentId, Board board, String nickname) {
        this.id = commentId;
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.description = board.getDescription();
        this.author = nickname;
        this.lastUpdate = board.getUpdatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.url = "/community/" + board.getGroupUrl() + "/board/" + id;
    }
}
