package com.huyeon.apiserver.config;

import com.huyeon.apiserver.model.Authority;
import com.huyeon.apiserver.model.dto.UserSignUpReq;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.repository.UserRepository;
import com.huyeon.apiserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TestDBInit implements CommandLineRunner {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        authService.signUp(UserSignUpReq.builder()
                .name("TEST_USER")
                .email("user@test.com")
                .password("1234")
                .birthday(LocalDate.of(2022, 7, 15))
                .build());

        authService.signUp(UserSignUpReq.builder()
                .name("TEST_SUBSCRIBER")
                .email("sub@test.com")
                .password("1234")
                .birthday(LocalDate.of(2022, 7, 15))
                .build());

        authService.signUp(UserSignUpReq.builder()
                .name("TEST_ADMIN")
                .email("admin@test.com")
                .password("1234")
                .birthday(LocalDate.of(2022, 7, 15))
                .build());

        Optional<User> admin = userRepository.findByEmail("admin@test.com");
        admin.ifPresent(user -> {
            user.getAuthorities().add(new Authority(Authority.ROLE_ADMIN));
            userRepository.save(user);
        });
    }
}
