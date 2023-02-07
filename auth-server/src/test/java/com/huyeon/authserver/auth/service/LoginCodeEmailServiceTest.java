package com.huyeon.authserver.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class LoginCodeEmailServiceTest {
    @Autowired
    private LoginCodeEmailService service;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("로그인 코드 발송")
    void send() {
        //given
        String email = "huyeon123@naver.com";

        //when
        service.send(email);

        //then
        String loginCode = redisTemplate.opsForValue().get("loginCode:" + email);
        assertNotNull(loginCode);
        System.out.println(loginCode);
    }
}
