package com.huyeon.apiserver.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huyeon.apiserver.model.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String title;
    private String author;
    private String location;
    private LocalDateTime date;
    @JsonProperty("time")
    private String uploadTime;
    private String comment;
    private String url;

    public CommentDto(Comment comment) {
        id = comment.getId();
        author = comment.getUserName();
        date = comment.getUpdatedAt();
        this.comment = comment.getComment();
    }
}
