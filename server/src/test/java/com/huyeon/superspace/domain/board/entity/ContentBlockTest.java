package com.huyeon.superspace.domain.board.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentBlockTest {
    @Test
    @DisplayName("ContentBlock 생성")
    void build() {
        //given
        Board board = Board.builder()
                .title("TEST")
                .build();

        //when, then
        assertDoesNotThrow(() -> ContentBlock.builder()
                .content("Hi!")
                .build());

        assertDoesNotThrow(() -> new ContentBlock());

        assertDoesNotThrow(() -> {
            ContentBlock contentBlock = new ContentBlock(board);
            assertEquals(board, contentBlock.getBoard());
        });
    }


}
