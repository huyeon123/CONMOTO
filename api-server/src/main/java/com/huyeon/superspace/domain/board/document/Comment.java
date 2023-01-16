package com.huyeon.superspace.domain.board.document;

import com.huyeon.superspace.domain.board.dto.CommentDto;
import com.huyeon.superspace.global.model.DocumentAudit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "comments")
public class Comment extends DocumentAudit {
    @Id
    private String id;
    private String boardId;
    private String author;
    private String body;
    private String tag;

    public Comment(CommentDto dto) {
        id = dto.getId();
        boardId = dto.getBoardId();
        author = dto.getAuthor();
        body = dto.getBody();
        tag = dto.getTag();
    }
}
