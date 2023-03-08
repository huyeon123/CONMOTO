package com.huyeon.superspace.domain.board.document;

import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.global.model.DocumentAudit;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "categories")
public class Category extends DocumentAudit {
    @Id
    private String id;

    @Field("group_url")
    private String groupUrl;

    private String name;

    private String description;

    private Type type;

    private boolean fold; //Type이 CATEGORY_GROUP일 경우만 지정 가능

    private boolean indent; //Type이 CATEGORY_GROUP이 아닐 경우만 지정 가능

    /* 아래 3개 필드는 Type이 CATEGORY_GROUP이 아닐 경우만 지정 가능*/
    @Field("available_write_level")
    private int availableWriteLevel;

    @Field("available_comment_level")
    private int availableCommentLevel;

    @Field("available_read_level")
    private int availableReadLevel;

    public Category(CategoryDto category) {
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

    public enum Type {
        CATEGORY_GROUP, ONLY_READ, NOTICE, BASIC
    }
}
