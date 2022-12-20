package com.huyeon.superspace.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.repository.UserRepository;
import com.huyeon.superspace.domain.user.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

    @Autowired
    UserService userService;

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
                .andExpect(result -> {
                    cookie = result.getResponse().getCookie("remember-me");
                });
    }

    @Test
    @Transactional
    @DisplayName("회원정보 수정")
    void editInfo() throws Exception {
        //given
        User changeUser = getUserInstance();

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/user/edit")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeUser))
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    private User getUserInstance() {
        LocalDate localDate = LocalDate.now();
        String email = "test@test.com";
        String password = "12345";
        String newName = "CHANGE_USER";

        return User.builder()
                .email(email)
                .name(newName)
                .password(password)
                .birthday(localDate)
                .build();
    }

    @Test
    @Transactional
    @DisplayName("회원탈퇴")
    void resign() throws Exception {
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/user/resign")
                        .cookie(cookie)
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("회원탈퇴 취소")
    void cancelResign() throws Exception {
        //given
        userService.resign("test@test.com");

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/user/resign")
                        .cookie(cookie)
        );

        //then
        resultActions.andExpect(status().isOk());
    }
}
