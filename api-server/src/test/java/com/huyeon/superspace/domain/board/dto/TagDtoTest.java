package com.huyeon.superspace.domain.board.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TagDtoTest {
    @Test
    @DisplayName("Tag 생성")
    void createTagDto(){
        //given
        String tag = "태그";

        //when
        assertDoesNotThrow(() -> new TagDto(tag));
    }
}
