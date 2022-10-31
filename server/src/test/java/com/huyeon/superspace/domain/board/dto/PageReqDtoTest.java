package com.huyeon.superspace.domain.board.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PageReqDtoTest {
    @Test
    @DisplayName("PageReqDto 생성")
    void createDto(){
        //when, then
        assertDoesNotThrow(() -> new PageReqDto());
        assertDoesNotThrow(() -> new PageReqDto("query", LocalDateTime.now(), 0));
    }
}
