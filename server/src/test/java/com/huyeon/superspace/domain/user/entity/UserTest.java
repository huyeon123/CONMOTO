package com.huyeon.superspace.domain.user.entity;

import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class UserTest {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("User 생성")
    void createUser(){
        //given
        UserSignUpReq request = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);

        //when, then
        assertDoesNotThrow(() -> new User(request));
    }

    @Test
    @DisplayName("비밀번호 암호화")
    void encryptPassword(){
        //given
        UserSignUpReq request = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);
        User user = new User(request);

        //when, then
        assertDoesNotThrow(() -> user.encryptPassword(passwordEncoder));
    }

    @Test
    @DisplayName("권한 확인")
    void getAuthorities(){
        //given
        UserSignUpReq request = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);
        User user = new User(request);

        //when
        Set<Authority> authorities = user.getAuthorities();

        //then
        assertEquals(1, authorities.size());
        assertDoesNotThrow(() -> {
            for (Authority authority : authorities) {
                String role = authority.getAuthority();
                assertEquals(role, Authority.ROLE_USER);
            }
        });
    }

    @Test
    @DisplayName("권한 추가")
    void addAuthority(){
        //given
        UserSignUpReq request = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);
        User user = new User(request);
        Set<Authority> authorities = user.getAuthorities();

        //when, then
        assertDoesNotThrow(() -> authorities.add(new Authority(Authority.ROLE_SUBSCRIBER)));
    }

    @Test
    @DisplayName("ToString 테스트")
    void toStringTest(){
        //given
        UserSignUpReq request = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);
        User user = new User(request);

        //when, then
        assertDoesNotThrow(() -> {
            System.out.println(user);
        });
    }
}
