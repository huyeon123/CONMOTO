package com.huyeon.authserver.auth.service;

import com.huyeon.authserver.auth.dto.EmailDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class MailSendTest {
    @Autowired
    MailSend mailSend;

    @Test
    @DisplayName("메일 발송 테스트")
    public void test() {
        //given
        EmailDTO test = new EmailDTO("huyeon123@naver.com", "TEST", "Hi! This is Conmoto");

        //when, then
        assertDoesNotThrow(() -> mailSend.send(test)) ;
    }
}
