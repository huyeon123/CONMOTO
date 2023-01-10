package com.huyeon.superspace.domain.newboard.document;

import com.huyeon.superspace.domain.newboard.dto.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "categories")
public class Category extends DocumentAudit {
    @Id
    private String id;

    private String groupUrl;

    @Indexed(unique = true)
    private String name;

    @DBRef
    @Field("parent_category")
    private Category parentCategory;

    public Category(CategoryDto dto) {
        id = dto.getId();
        groupUrl = dto.getGroupUrl();
        name = dto.getName();

        if (Objects.nonNull(dto.getParent())) {
            parentCategory = new Category(dto.getParent());
        }
    }
}
