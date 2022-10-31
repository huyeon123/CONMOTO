package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.entity.Category;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long categoryId;
    private String name;
    private Long parentId;
    private int parentOrder;
    private List<CategoryDto> subCategories;

    public CategoryDto(Long categoryId, String name, int parentOrder) {
        this.categoryId = categoryId;
        this.name = name;
        this.parentOrder = parentOrder;
    }

    @Builder
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
