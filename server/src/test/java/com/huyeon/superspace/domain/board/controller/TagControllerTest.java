package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.dto.TagDto;
import com.huyeon.superspace.domain.board.service.TagService;
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
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class TagControllerTest {

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
    @DisplayName("태그 변경")
    void editTag() throws Exception {
        //given
        List<TagDto> request = List.of(new TagDto("태그1"), new TagDto("태그2"));

        //when
        ResultActions resultActions = util.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/tag?boardId=" + boardId)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(util.objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isOk());
    }
}
