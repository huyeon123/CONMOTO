package com.huyeon.superspace.domain.auth.service;

import com.huyeon.superspace.domain.auth.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
public class AuthServiceTest {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @DisplayName("")
    @Test
    void test_1(){
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@naver.com");
        System.out.println(userDetails);
    }
}
