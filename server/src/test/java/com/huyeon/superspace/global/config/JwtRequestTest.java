package com.huyeon.superspace.global.config;

import com.huyeon.superspace.domain.auth.dto.UserSignInReq;
import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import com.huyeon.superspace.domain.auth.service.AuthService;
import com.huyeon.superspace.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtRequestTest{

    @LocalServerPort
    int port;

    @Autowired
    AuthService authService;

    @BeforeEach
    void init() {
        UserSignUpReq request = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);
        authService.signUp(request);
    }

    @Test
    @DisplayName("로그인 테스트")
    void jwtLogin(){
        //given
        RestTemplate client = new RestTemplate();
        HttpEntity<UserSignInReq> body = new HttpEntity<>(
                new UserSignInReq("test@test.com", "12345", false)
        );

        //when
        ResponseEntity<User> response = client.exchange(URI.create("http://localhost:" + port + "/auth/login"), HttpMethod.POST, body, User.class);
        System.out.println(response.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));
    }

}
