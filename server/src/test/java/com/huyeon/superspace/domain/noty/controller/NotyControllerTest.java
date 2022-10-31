package com.huyeon.superspace.domain.noty.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huyeon.superspace.domain.noty.dto.NotyDto;
import com.huyeon.superspace.domain.noty.entity.Noty;
import com.huyeon.superspace.domain.noty.entity.NotyType;
import com.huyeon.superspace.domain.noty.entity.ReceivedNoty;
import com.huyeon.superspace.domain.noty.repository.NotyReceiverRepository;
import com.huyeon.superspace.domain.noty.repository.NotyRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
public class NotyControllerTest {

    @Autowired
    NotyRepository notyRepository;

    @Autowired
    NotyReceiverRepository receiverRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    Cookie cookie;

    @BeforeAll
    void login() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/auth/login?username=test@test.com&password=12345&remember-me=true")
                )
                .andExpect(cookie().exists("remember-me"))
                .andExpect(result -> cookie = result.getResponse().getCookie("remember-me"));
    }

    @Test
    @DisplayName("알림 Subscribe 테스트")
    void subscribe() throws Exception {
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/noty/subscribe")
                        .cookie(cookie)
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("사용자의 최신 알림을 페이지 별로 조회")
    void userLatestNoty() throws Exception {
        //given
        createTestReceivedNoty();
        int page = 0;

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/noty/" + page)
                        .cookie(cookie)
        );

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    List<NotyDto> latestNoty =
                            objectMapper.readValue(contentAsString, new TypeReference<>() {});
                    assertFalse(latestNoty.isEmpty());
                });
    }

    private Long createTestReceivedNoty() {
        Noty noty = createTestNoty();

        ReceivedNoty receivedNoty = ReceivedNoty.builder()
                .userEmail("test@test.com")
                .noty(noty)
                .isRead(false)
                .build();

        return receiverRepository.save(receivedNoty).getId();
    }

    private Noty createTestNoty() {
        Noty noty = Noty.builder()
                .senderName("sub@test.com")
                .message("Hi!")
                .type(NotyType.DIRECT_MESSAGE)
                .build();

        return  notyRepository.save(noty);
    }

    @Test
    @DisplayName("미수신 알림 조회")
    void unreadNoty() throws Exception {
        //given
        createTestReceivedNoty();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/noty/unread")
                        .cookie(cookie)
        );

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    List<NotyDto> unreadEvent =
                            objectMapper.readValue(contentAsString, new TypeReference<>() {});
                    assertFalse(unreadEvent.isEmpty());
                });
    }

    @Test
    @DisplayName("알림 읽음 체크")
    void setReadNoty() throws Exception {
        //given
        Long notyId = createTestReceivedNoty();
        List<Long> request = List.of(notyId);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/noty")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isOk());
    }
}
