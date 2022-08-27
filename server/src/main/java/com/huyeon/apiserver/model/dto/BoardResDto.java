package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.BoardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResDto {
    private Long id;

    private String title;

    private String description;

    private String categoryName;

    private BoardStatus status;

    private List<TagDto> tags;

    private List<CommentDto> comments;

    private List<ContentDto> contents;

    private String url;
}
