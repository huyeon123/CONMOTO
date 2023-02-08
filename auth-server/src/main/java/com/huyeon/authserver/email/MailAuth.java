package com.huyeon.authserver.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

@Component
public class MailAuth extends Authenticator {
    private final PasswordAuthentication passwordAuthentication;

    public MailAuth(
            @Value("${gmail.email}") String email,
            @Value("${gmail.password}") String password
    ) {
        passwordAuthentication = new PasswordAuthentication(email, password);
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return passwordAuthentication;
    }
}
