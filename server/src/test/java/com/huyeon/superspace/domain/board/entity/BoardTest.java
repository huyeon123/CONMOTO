package com.huyeon.superspace.domain.board.entity;

import com.huyeon.superspace.domain.group.entity.WorkGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    @DisplayName("Board 생성")
    void createBoard() {
        //when, then
        assertDoesNotThrow(this::build);
        assertDoesNotThrow(() -> new Board());
    }

    private Board build() {
        return Board.builder()
                .userEmail("test@test.com")
                .title("test")
                .description("test")
                .build();
    }

    @Test
    @DisplayName("NonNull Test")
    void titleMustBeNonNull() {
        //when
        assertThrows(NullPointerException.class,
                () -> Board.builder()
                        .userEmail("test@test.com")
                        .build());
    }

    @Test
    @DisplayName("Group 등록 후 그룹명, 그룹url 조회 테스트")
    void groupTest(){
        //given
        Board board = build();
        WorkGroup group = WorkGroup.builder()
                .name("테스트그룹")
                .urlPath("test-group")
                .build();

        //when
        board.setGroup(group);

        //then
        assertEquals(group.getName(), board.getGroupName());
        assertEquals(group.getUrlPath(), board.getGroupUrl());
    }

    @Test
    @DisplayName("카테고리 등록 후 카테고리명 조회 테스트")
    void categoryTest(){
        //given
        Board board = build();
        Category category = Category.builder()
                .name("테스트 카테고리")
                .build();

        //when
        board.setCategory(category);

        //then
        assertEquals(category.getName(), board.getCategoryName());
    }
}
