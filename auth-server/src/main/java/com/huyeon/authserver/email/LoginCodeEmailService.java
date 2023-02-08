package com.huyeon.authserver.email;

import com.huyeon.authserver.auth.dto.EmailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginCodeEmailService implements EmailService {
    private final MailSend mailSend;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void send(String email) {
        String loginCode = generateTempLoginCode();
        saveLoginCode(email, loginCode);
        EmailDTO request = generateDTO(email, loginCode);
        mailSend.send(request);
    }

    private String generateTempLoginCode() {
        String randomAlphabetic = RandomStringUtils.randomAlphabetic(16);
        StringBuilder sb = new StringBuilder(randomAlphabetic);
        for (int i = 1; i <= 3; i++) {
            sb.insert(i * 4 + (i - 1), "-");
        }
        return sb.toString();
    }

    private void saveLoginCode(String email, String loginCode) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("loginCode:" + email, loginCode, 30, TimeUnit.MINUTES);
    }

    public EmailDTO generateDTO(String to, String body) {
        final String title = "[CONMOTO] 임시 로그인 코드는 " + body + " 입니다.";
        return new EmailDTO(to, title, body);
    }
}
