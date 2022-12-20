package com.huyeon.authserver.auth.service;

import com.huyeon.authserver.auth.dto.UserSignInReq;
import com.huyeon.authserver.auth.dto.UserSignUpReq;
import com.huyeon.authserver.auth.dto.UserTokenInfo;
import com.huyeon.authserver.auth.entity.User;
import com.huyeon.authserver.auth.repository.AuthRepository;
import com.huyeon.authserver.jwt.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserDetailsServiceImpl userDetailsService;

    @Transactional
    public void signUp(UserSignUpReq request) {
        User user = new User(request);
        user.encryptPassword(passwordEncoder);
        authRepository.save(user);
    }

    //로그인
    public UserTokenInfo logIn(UserSignInReq request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);
//        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenProvider.createToken(authentication);
    }

    //이메일 중복체크
    public boolean isDuplicateEmail(String email) {
        return authRepository.existsByEmail(email);
    }

    public String generateNewAccessToken(String refreshToken) {
        int delimiterIndex = refreshToken.indexOf("-");
        String userEmail = refreshToken.substring(0, delimiterIndex);
        refreshToken = refreshToken.substring(delimiterIndex + 1);

        //Todo: Redis에서 userEmail로 RefreshToken 조회 후 매칭확인

        if (tokenProvider.isExpired(refreshToken)) return null;

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        return tokenProvider.createAccessToken(userDetails, new Date().getTime());
    }
}
