package com.huyeon.superspace.domain.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserSignInReqTest {

    @Test
    @DisplayName("로그인 요청 시 DTO 생성 테스트")
    void userSignIn(){
        //given
        String email = "test@test.com";
        String password = "12345";
        boolean rememberMe = false;

        //when
        UserSignInReq request = new UserSignInReq(email, password, rememberMe);

        //then
        assertEquals(email, request.getEmail());
        assertEquals(password, request.getPassword());
        assertEquals(rememberMe, request.isRememberMe());
    }
}
