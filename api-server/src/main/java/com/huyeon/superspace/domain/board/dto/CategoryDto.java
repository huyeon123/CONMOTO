package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.document.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDto {
    private String id;

    private String groupUrl;

    private String name;

    private CategoryDto parent;

    private List<CategoryDto> children; //읽기용

    public CategoryDto(Category category) {
        id = category.getId();
        groupUrl = category.getGroupUrl();
        name = category.getName();

        if (Objects.nonNull(category.getParentCategory())) {
            parent = new CategoryDto(category.getParentCategory());
        }
    }
}
