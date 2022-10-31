package com.huyeon.superspace.domain.board.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoryTest {
    @Test
    @DisplayName("Category 생성")
    void build(){
        //when, then
        assertDoesNotThrow(() -> Category.builder()
                .name("테스트 카테고리")
                .build());

        assertDoesNotThrow(() -> new Category());
    }

    @Test
    @DisplayName("부모 카테고리 등록 후 부모 ID 조회")
    void parentCategoryTest(){
        //given
        Category parent = Category.builder()
                .id(1L)
                .name("부모 카테고리")
                .build();

        //when
        Category child = Category.builder()
                .id(2L)
                .name("자식 카테고리")
                .parent(parent)
                .build();

        //then
        assertEquals(parent.getId(), child.getParentId());
    }
}
