package com.huyeon.authserver.email;

import com.huyeon.authserver.auth.dto.EmailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailSend {
    @Value("${gmail.email}")
    private String sender;
    private final MailAuth mailAuth;

    public void send(EmailDTO request) {
        Properties prop = setProperties();

        Session session = Session.getDefaultInstance(prop, mailAuth);

        sendMessage(session, request);
    }

    private Properties setProperties() {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        props.put("mail.smtp.quitwait", "false");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        return props;
    }

    private void sendMessage(Session session, EmailDTO request) {
        try {
            MimeMessage message = createMessage(session, request);
            Transport.send(message);
        } catch (AddressException e) {
            log.error("올바른 주소가 아닙니다.");
        } catch (MessagingException e) {
            e.printStackTrace();
            log.error("메세지 생성에 실패했습니다.");
        } catch (UnsupportedEncodingException e) {
            log.error("지원하지 않는 인코딩 포맷입니다.");
        }
    }

    private MimeMessage createMessage(Session session, EmailDTO request)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage(session);

        InternetAddress to = new InternetAddress(request.getTo());
        message.setRecipient(Message.RecipientType.TO, to);
        message.setSubject(request.getTitle());
        message.setFrom(new InternetAddress(sender, "CONMOTO"));
        message.setSentDate(new Date());

        message.setText(request.getBody());

        return message;
    }
}
