package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.entity.ContentBlock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ContentDtoTest {
    @Test
    @DisplayName("ContentDto 생성")
    void createDto(){
        //given
        ContentBlock contentBlock = ContentBlock.builder()
                .id(1L)
                .content("테스트 내용")
                .build();

        //when, then
        assertDoesNotThrow(() -> new ContentDto("테스트 내용"));
        assertDoesNotThrow(() -> new ContentDto(contentBlock));
        assertDoesNotThrow(() -> ContentDto.builder()
                .content(contentBlock).build());
    }
}
