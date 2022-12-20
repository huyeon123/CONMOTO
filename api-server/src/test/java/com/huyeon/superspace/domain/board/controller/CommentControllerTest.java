package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.dto.CommentDto;
import com.huyeon.superspace.domain.board.dto.PageReqDto;
import com.huyeon.superspace.domain.board.service.CommentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class CommentControllerTest {

    @Autowired
    CommentService commentService;

    @Autowired
    ControllerTestUtil util;
    
    @Autowired
    MockMvc mockMvc;

    Cookie cookie;

    Long boardId;

    @BeforeAll
    void init() throws Exception {
        cookie = util.login(mockMvc);
        util.createTestGroup();
        boardId = util.createTestBoard();
    }

    @Test
    @DisplayName("댓글 생성")
    void createComment() throws Exception {
        //given
        CommentDto request = CommentDto.builder().comment("테스트 댓글").build();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/comment?boardId=" + boardId)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(util.objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("특정 사용자가 작성한 최신 댓글 조회")
    void getComment() throws Exception {
        //given
        createTestComment();
        PageReqDto request = new PageReqDto(null, LocalDateTime.now().plusSeconds(5), 0);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/comment/latest")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(util.objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    private Long createTestComment() {
        CommentDto commentDto = CommentDto.builder().comment("테스트 댓글").build();
        return commentService.createComment("test@test.com", boardId, commentDto);
    }

    @Test
    @DisplayName("댓글 수정")
    void editComment() throws Exception {
        //given
        Long commentId = createTestComment();
        CommentDto request = CommentDto.builder()
                .id(commentId)
                .comment("수정 댓글")
                .build();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/comment")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(util.objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 삭제")
    void removeComment() throws Exception {
        //given
        Long commentId = createTestComment();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/comment?commentId=" + commentId)
                        .cookie(cookie)
        );

        //then
        resultActions.andExpect(status().isOk());
    }
}
