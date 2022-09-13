package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.Category;
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
    private List<CategoryDto> subCategories;

    public CategoryDto(Long categoryId, String name, Long parentId) {
        this.categoryId = categoryId;
        this.name = name;
        this.parentId = parentId;
    }

    public CategoryDto(Category category) {
        this.categoryId = category.getId();
        this.name = category.getName();
        this.parentId = category.getParent() == null ? 0L : category.getParentId();
    }

    public List<CategoryDto> getSubCategories() {
        if(subCategories == null) subCategories = Collections.emptyList();
        return subCategories;
    }
}
