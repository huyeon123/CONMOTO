package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.entity.BoardStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class BoardHeaderDtoTest {
    @Test
    @DisplayName("BoardHeaderDto 생성")
    void createDto() {
        //when, then
        assertDoesNotThrow(() -> new BoardHeaderDto(
                1L,
                "제목",
                "설명",
                1L,
                BoardStatus.READY,
                "type")
        );

        assertDoesNotThrow(() -> BoardHeaderDto.builder()
                .id(1L)
                .target("category")
                .categoryId(2L));
    }
}
