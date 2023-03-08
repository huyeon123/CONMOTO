package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.document.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDto {
    private String id;

    private String groupUrl;

    private String name;

    private String description;

    private Category.Type type;

    private boolean favorite;

    private boolean fold;

    private boolean indent;

    private int availableWriteLevel;

    private int availableCommentLevel;

    private int availableReadLevel;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.groupUrl = category.getGroupUrl();
        this.name = category.getName();
        this.description = category.getDescription();
        this.type = category.getType();
        this.fold = category.isFold();
        this.indent = category.isIndent();
        this.availableWriteLevel = category.getAvailableWriteLevel();
        this.availableCommentLevel = category.getAvailableCommentLevel();
        this.availableReadLevel = category.getAvailableReadLevel();
    }
}
