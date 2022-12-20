package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoryDtoTest {
    @Test
    @DisplayName("CategoryDto 생성")
    void createDto() {
        //given
        Category category = Category.builder()
                .name("테스트 카테고리")
                .build();

        //when
        assertDoesNotThrow(() -> new CategoryDto(1L, "테스트 카테고리", -1));
        assertDoesNotThrow(() -> new CategoryDto(category));
        assertDoesNotThrow(() -> CategoryDto.builder().category(category).build());
    }

    @Test
    @DisplayName("Setter - subCategories")
    void setSubCategories() {
        //given
        CategoryDto dto = new CategoryDto(1L, "상위 카테고리", -1);
        List<CategoryDto> subCategories = List.of(new CategoryDto(2L, "하위 카테고리", 0));

        //when
        dto.setSubCategories(subCategories);

        //then
        assertEquals(subCategories, dto.getSubCategories());
    }

    @Test
    @DisplayName("Getter - subCategories")
    void getSubCategories() {
        //given
        CategoryDto dto = new CategoryDto(1L, "상위 카테고리", -1);

        //when
        List<CategoryDto> subCategories = dto.getSubCategories();

        //then
        assertTrue(subCategories.isEmpty());
    }
}
