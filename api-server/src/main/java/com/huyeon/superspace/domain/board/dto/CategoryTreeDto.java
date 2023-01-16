package com.huyeon.superspace.domain.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CategoryTreeDto {
    private String id;

    private String groupUrl;

    private String name;

    private List<CategoryTreeDto> children;
}
