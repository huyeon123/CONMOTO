package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.BoardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResDto {
    private Long id;

    private String author;

    private String title;

    private String description;

    private String categoryName;

    private BoardStatus status;

    private List<TagDto> tags;

    private List<CommentDto> comments;

    private List<ContentDto> contents;

    private String url;

    private LocalDateTime updatedAt;

    public BoardResDto(Board board) {
        id = board.getId();
        author = board.getUserEmail();
        title = board.getTitle();
        description = board.getDescription();
        status = board.getStatus();
        updatedAt = board.getUpdatedAt();

        if (board.getCategory() != null) {
            categoryName = board.getCategoryName();
        }
    }
}
