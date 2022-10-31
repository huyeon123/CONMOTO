package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.dto.ContentDto;
import com.huyeon.superspace.domain.board.service.ContentBlockService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class ContentsControllerTest {

    @Autowired
    ContentBlockService contentService;

    @Autowired
    ControllerTestUtil util;

    Cookie cookie;

    Long boardId;

    @BeforeAll
    void init() throws Exception {
        cookie = util.login();
        util.createTestGroup();
        boardId = util.createTestBoard();
    }

    @Test
    @DisplayName("컨텐츠 생성")
    void createContent() throws Exception {
        //when
        ResultActions resultActions = util.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/contents/" + boardId)
                        .cookie(cookie)
        );

        //then
        resultActions.andExpect(status().isCreated())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    Long id = util.objectMapper.readValue(contentAsString, Long.class);
                    assertTrue(id > 0);
                });
    }

    @Test
    @DisplayName("컨텐츠 수정")
    void editContent() throws Exception {
        //given
        Long contentId = contentService.createContent(boardId);
        ContentDto request = new ContentDto("테스트 컨텐츠");

        //when
        ResultActions resultActions = util.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/contents/" + contentId)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(util.objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("컨텐츠 삭제")
    void deleteContent() throws Exception {
        //given
        Long contentId = contentService.createContent(boardId);

        //when
        ResultActions resultActions = util.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/contents/" + contentId)
                        .cookie(cookie)
        );

        //then
        resultActions.andExpect(status().isOk());
    }
}
