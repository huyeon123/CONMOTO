package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.entity.Board;
import com.huyeon.superspace.domain.board.entity.BoardStatus;
import com.huyeon.superspace.domain.board.entity.Category;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardDtoTest {
    @Test
    @DisplayName("BoardDto 생성")
    void createBoardDto() {
        //given
        Board board = testBoardInstance();

        //when
        BoardDto dto = new BoardDto(board);

        //then
        assertEquals(board.getId(), dto.getId());
        assertEquals(board.getUserEmail(), dto.getAuthor());
        assertEquals(board.getTitle(), dto.getTitle());
        assertEquals(board.getDescription(), dto.getDescription());
        assertEquals(board.getGroupName(), dto.getGroupName());
        assertEquals(board.getStatus(), dto.getStatus());
        assertEquals(board.getUpdatedAt(), dto.getUpdatedAt());
        assertTrue(dto.getUrl().contains(board.getGroupUrl()));
        assertEquals(board.getCategoryName(), dto.getCategoryName());
    }
    
    private Board testBoardInstance() {
        WorkGroup group = WorkGroup.builder()
                .name("테스트그룹")
                .urlPath("test-group")
                .build();

        Category category = Category.builder()
                .name("테스트 카테고리")
                .build();

        return Board.builder()
                .id(1L)
                .userEmail("test@test.com")
                .title("제목")
                .status(BoardStatus.READY)
                .description("설명")
                .group(group)
                .category(category)
                .build();
    }
    
    @Test
    @DisplayName("Setter - Tag")
    void setTags(){
        //given
        Board board = testBoardInstance();
        BoardDto dto = new BoardDto(board);
        List<TagDto> tags = List.of(new TagDto("태그"));

        //when
        dto.setTags(tags);
        
        //then
        assertEquals(tags, dto.getTags());
    }

    @Test
    @DisplayName("Setter - Comment")
    void setComments(){
        //given
        Board board = testBoardInstance();
        BoardDto dto = new BoardDto(board);
        List<CommentDto> comments = List.of(
                CommentDto.builder()
                        .comment("테스트 댓글")
                        .build()
        );

        //when
        dto.setComments(comments);

        //then
        assertEquals(comments, dto.getComments());
    }

    @Test
    @DisplayName("Setter - Content")
    void setContents(){
        //given
        Board board = testBoardInstance();
        BoardDto dto = new BoardDto(board);
        List<ContentDto> contents = List.of(new ContentDto("테스트 내용"));

        //when
        dto.setContents(contents);

        //then
        assertEquals(contents, dto.getContents());
    }
}
