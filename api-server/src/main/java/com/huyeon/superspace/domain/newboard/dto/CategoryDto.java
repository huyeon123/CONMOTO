package com.huyeon.superspace.domain.newboard.dto;

import com.huyeon.superspace.domain.newboard.document.Category;
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

    private CategoryDto parent; //TODO: 반환 시 해당 필드는 JSON에 포함하지 않음.

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
