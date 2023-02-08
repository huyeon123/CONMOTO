package com.huyeon.authserver.auth.service;

import com.huyeon.authserver.email.MailAuth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.PasswordAuthentication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class MailAuthTest {
    @Autowired
    MailAuth mailAuth;

    @Test
    @DisplayName("메일 발송용 Credential 확인")
    void findUser(){
        //when
        PasswordAuthentication passwordAuthentication = mailAuth.getPasswordAuthentication();

        //then
        assertDoesNotThrow(() -> System.out.println(passwordAuthentication));
    }
}
