package com.huyeon.superspace.domain.auth.service;

import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserDetailsServiceImplTest {

    @Autowired
    AuthService authService;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("UserDetails 생성")
    void loadUserByUsername(){
        //given
        UserSignUpReq signUpReq = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);
        authService.signUp(signUpReq);
        String email = "test@test.com";

        //when, then
        assertDoesNotThrow(() -> {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            assertEquals(email, userDetails.getUsername());
        });
    }


}
