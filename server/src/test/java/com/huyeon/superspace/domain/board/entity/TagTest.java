package com.huyeon.superspace.domain.board.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TagTest {
    @Test
    @DisplayName("Tag 생성")
    void build(){
        //given
        Board board = Board.builder().title("TEST").build();

        //when, then
        assertDoesNotThrow(() -> new Tag());

        assertDoesNotThrow(() -> new Tag(1L, board, "태그"));

        assertDoesNotThrow(() -> Tag.builder()
                .board(board)
                .tag("태그"));
    }
}
