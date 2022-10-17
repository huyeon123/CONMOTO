package com.huyeon.superspace.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huyeon.superspace.domain.auth.dto.UserSignInReq;
import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import com.huyeon.superspace.domain.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.transaction.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ComponentScan
@AutoConfigureMockMvc
@SpringBootTest
public class AuthControllerTest {

    @Autowired
    AuthService authService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입")
    void signUp() throws Exception {
        //given
        UserSignUpReq request = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("로그인")
    void logIn() throws Exception {
        //given
        UserSignUpReq signUpReq = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);
        authService.signUp(signUpReq);
        UserSignInReq signInReq = new UserSignInReq("test@test.com", "12345", false);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInReq))
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("이메일 중복체크")
    void checkDuplicateEmail() throws Exception {
        //given
        String email = "test@test.com";
        String expectedContent = objectMapper.writeValueAsString(true);
        UserSignUpReq signUpReq = new UserSignUpReq("TEST_USER", email, "12345", null);
        authService.signUp(signUpReq);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(email)
        );

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent));
    }
}
