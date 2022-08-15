package com.huyeon.apiserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long categoryId;
    private String name;
    private Long parentId;
    private Integer level;
    private List<CategoryDto> subCategories;

    public CategoryDto(Long categoryId, String name, Long parentId) {
        this.categoryId = categoryId;
        this.name = name;
        this.parentId = parentId;
    }

    public List<CategoryDto> getSubCategories() {
        if(subCategories == null) subCategories = Collections.emptyList();
        return subCategories;
    }
}
