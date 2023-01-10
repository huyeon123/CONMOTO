package com.huyeon.superspace.domain.newboard.dto;

import com.huyeon.superspace.domain.newboard.document.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {
    private String id;
    private String boardId;
    private String author;
    private String body;
    private String tag;

    public CommentDto(Comment comment) {
        id = comment.getId();
        boardId = comment.getBoardId();
        author = comment.getAuthor();
        body = comment.getBody();
        tag = comment.getTag();
    }
}
