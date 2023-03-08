package com.huyeon.superspace.domain.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ContentUpdateDto {
    private Long boardId;
    private String markdown;
}
