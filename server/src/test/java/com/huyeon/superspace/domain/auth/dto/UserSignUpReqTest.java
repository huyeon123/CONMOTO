package com.huyeon.superspace.domain.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserSignUpReqTest {

    @Test
    @DisplayName("회원가입 요청 시 DTO 생성 테스트")
    void userSignUp(){
        //given
        String name = "TEST_USER";
        String email = "test@test.com";
        String password = "12345";
        LocalDate birthday = LocalDate.now();

        //when
        UserSignUpReq request = new UserSignUpReq(name, email, password, birthday);

        //then
        assertEquals(name, request.getName());
        assertEquals(email, request.getEmail());
        assertEquals(password, request.getPassword());
        assertEquals(birthday, request.getBirthday());
    }

    @Test
    @DisplayName("회원가입 요청 시 DTO 생성 테스트(생일 입력안함)")
    void userSignUpWithoutBirthday(){
        //given
        String name = "TEST_USER";
        String email = "test@test.com";
        String password = "12345";

        //when
        UserSignUpReq request = new UserSignUpReq(name, email, password, null);

        //then
        assertEquals(name, request.getName());
        assertEquals(email, request.getEmail());
        assertEquals(password, request.getPassword());
        assertNull(request.getBirthday());
    }
}
