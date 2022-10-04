package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.BoardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BoardHeaderDto {
    private Long id;

    private String title;

    private String description;

    private Long categoryId;

    private BoardStatus status;

    private String target;
}
