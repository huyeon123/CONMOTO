package com.huyeon.superspace.domain.auth.service;

import com.huyeon.superspace.domain.auth.dto.UserSignInReq;
import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import com.huyeon.superspace.domain.auth.repository.AuthRepository;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.global.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public void signUp(UserSignUpReq request) {
        User user = new User(request);
        user.encryptPassword(passwordEncoder);
        authRepository.save(user);
    }

    //로그인
    public String logIn(UserSignInReq request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenProvider.createToken(authentication);
    }

    //이메일 중복체크
    public boolean isDuplicateEmail(String email) {
        return authRepository.existsByEmail(email);
    }
}
