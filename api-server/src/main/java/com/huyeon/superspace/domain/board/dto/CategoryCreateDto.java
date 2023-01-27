package com.huyeon.superspace.domain.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryCreateDto {
    private String groupUrl;
    private String name;
    private String parentId;
}
