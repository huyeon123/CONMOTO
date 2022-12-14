package com.huyeon.authserver.auth.repository;

import com.huyeon.authserver.auth.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AuthRepoTest {
    @Autowired
    AuthRepository authRepository;

    @Test
    @DisplayName("DB에서 사용자 정보 조회")
    void findUser(){
        //given
        String email = "test@test.com";

        //when
        Optional<User> user = authRepository.findByEmail(email);

        //then
        assertTrue(user.isPresent());
        assertEquals(email, user.get().getEmail());
    }
}
