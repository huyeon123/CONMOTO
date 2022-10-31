package com.huyeon.superspace.domain.board.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.BoardHeaderDto;
import com.huyeon.superspace.domain.board.dto.PageReqDto;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class BoardControllerTest {

    @Autowired
    ControllerTestUtil util;

    @Autowired
    MockMvc mockMvc;

    Cookie cookie;

    @BeforeAll
    void init() throws Exception {
        cookie = util.login(mockMvc);
        util.createTestGroup();
    }

    @Test
    @DisplayName("게시글 생성")
    void createBoard() throws Exception {
        //given
        String groupUrl = "test-group";

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/board/" + groupUrl + "/create")
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
    @DisplayName("게시글 조회")
    void getBoard() throws Exception {
        //given
        Long boardId = util.createTestBoard();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/board/" + boardId)
                        .cookie(cookie)
        );

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    BoardDto board = util.objectMapper.readValue(contentAsString, BoardDto.class);
                    assertEquals(boardId, board.getId());
                });
    }

    @Test
    @DisplayName("게시글 수정")
    void editBoard() throws Exception {
        //given
        Long boardId = util.createTestBoard();
        BoardHeaderDto request = BoardHeaderDto.builder()
                .id(boardId)
                .title("제목 수정")
                .target("title")
                .build();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/board/" + boardId)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(util.objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 삭제")
    void removeBoard() throws Exception {
        //given
        Long boardId = util.createTestBoard();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/board/" + boardId)
                        .cookie(cookie)
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("그룹 내 최신 게시글 조회")
    void getLatestBoardInGroup() throws Exception {
        //given
        util.createTestBoard();
        String groupUrl = "test-group";
        PageReqDto request = new PageReqDto(groupUrl, LocalDateTime.now().plusSeconds(5), 0);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/board/latest/group")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(util.objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    List<BoardDto> latest = util.objectMapper.readValue(contentAsString, new TypeReference<>() {
                    });
                    assertFalse(latest.isEmpty());
                    latest.forEach(dto -> assertTrue(dto.getUrl().contains(groupUrl)));
                });
    }

    @Test
    @DisplayName("카테고리 내 최신 게시글 조회")
    void getLatestBoardInCategory() throws Exception {
        //given
        util.createTestBoard();
        String categoryName = "==최상위 카테고리==";
        PageReqDto request = new PageReqDto(categoryName, LocalDateTime.now().plusSeconds(5), 0);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/board/latest/category")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(util.objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    List<BoardDto> latest = util.objectMapper.readValue(contentAsString, new TypeReference<>() {
                    });
                    assertFalse(latest.isEmpty());
                    latest.forEach(dto -> assertEquals(categoryName, dto.getCategoryName()));
                });
    }

    @Test
    @DisplayName("특정 사용자가 작성한 최신 게시글 조회")
    void getLatestBoardInUser() throws Exception {
        //given
        util.createTestBoard();
        String email = "test@test.com";
        PageReqDto request = new PageReqDto(null, LocalDateTime.now().plusSeconds(5), 0);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/board/latest/user")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(util.objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    List<BoardDto> latest = util.objectMapper.readValue(contentAsString, new TypeReference<>() {
                    });
                    assertFalse(latest.isEmpty());
                    latest.forEach(dto -> assertEquals(email, dto.getAuthor()));
                });
    }
}
