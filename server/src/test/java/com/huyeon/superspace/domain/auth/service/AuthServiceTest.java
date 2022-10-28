package com.huyeon.superspace.domain.auth.service;

import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Test
    @Transactional
    @DisplayName("회원가입")
    void signUp(){
        //given
        UserSignUpReq request = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);

        //when, then
        assertDoesNotThrow(() -> authService.signUp(request));
    }

    @Test
    @Transactional
    @DisplayName("이메일 중복체크")
    void isDuplicateEmail(){
        //given
        UserSignUpReq signUpReq = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);
        authService.signUp(signUpReq);
        String email = "test@test.com";

        //when
        boolean isDuplicate = authService.isDuplicateEmail(email);

        //then
        assertTrue(isDuplicate);
    }
}
