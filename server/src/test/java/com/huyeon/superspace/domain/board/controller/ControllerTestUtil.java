package com.huyeon.superspace.domain.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huyeon.superspace.domain.board.dto.BoardHeaderDto;
import com.huyeon.superspace.domain.board.service.BoardService;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

@Component
@SpringBootTest
class ControllerTestUtil {

    @Autowired
    GroupService groupService;

    @Autowired
    BoardService boardService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    protected Cookie login() throws Exception {
        Cookie[] cookie = new Cookie[1];
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/auth/login?username=test@test.com&password=12345&remember-me=true")
                )
                .andExpect(cookie().exists("remember-me"))
                .andExpect(result -> cookie[0] = result.getResponse().getCookie("remember-me"));

        return cookie[0];
    }

    protected GroupDto createTestGroup() {
        String email = "test@test.com";
        GroupDto request = new GroupDto("테스트그룹", "test-group", "테스트입니다.");

        return groupService.createGroup(email, request);
    }

    protected Long createTestBoard() {
        String email = "test@test.com";
        String groupUrl = "test-group";
        Long boardId = boardService.createBoard(email, groupUrl);

        BoardHeaderDto categoryChangeReq = BoardHeaderDto.builder()
                .id(boardId)
                .target("category")
                .categoryId(1L)
                .build();

        boardService.editBoard(categoryChangeReq);

        return boardId;
    }
}
