package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.entity.BoardStatus;
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
