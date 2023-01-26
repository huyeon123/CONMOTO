package com.huyeon.authserver.auth.service;

import com.huyeon.authserver.auth.dto.UserSignInReq;
import com.huyeon.authserver.auth.dto.UserSignUpReq;
import com.huyeon.authserver.auth.dto.UserTokenInfo;
import com.huyeon.authserver.auth.entity.User;
import com.huyeon.authserver.auth.exception.DuplicateEmailException;
import com.huyeon.authserver.auth.repository.AuthRepository;
import com.huyeon.authserver.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserDetailsServiceImpl userDetailsService;

    @Transactional
    public void signUp(UserSignUpReq request) {
        if (isDuplicateEmail(request.getEmail())) throw new DuplicateEmailException();

        User user = new User(request);
        user.encryptPassword(passwordEncoder);
        authRepository.save(user);
    }

    @Transactional
    public UserTokenInfo logIn(UserSignInReq request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);

            UserTokenInfo tokenSet = tokenProvider.createToken(authentication);

            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            ops.set(request.getEmail(), tokenSet.getRefreshToken(), 7, TimeUnit.DAYS);
            return tokenSet;
        } catch (BadCredentialsException e) {
            String message = "아이디/비밀번호가 일치하지 않습니다.";
            throw new BadCredentialsException(message);
        } catch (RedisConnectionFailureException e) {
            String message = "Redis가 shutdown 상태입니다!! 토큰을 저장하기 위해 Redis를 활성화하십시오.";
            throw new RedisConnectionFailureException(message);
        }

    }

    public boolean isDuplicateEmail(String email) {
        return authRepository.existsByEmail(email);
    }

    @Transactional
    public String generateNewAccessToken(String refreshToken) {
        String userEmail = tokenProvider.getSubject(refreshToken);

        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String serverToken = ops.get(userEmail);

        if (isDiffToken(serverToken, refreshToken)) {
            log.info("토큰 탈취 의심 - Redis에 저장된 refreshToken을 삭제합니다.");
            ops.getAndDelete(userEmail);
            return null;
        }

        if (tokenProvider.isExpired(refreshToken)) return null;

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        return tokenProvider.createAccessToken(userDetails, new Date().getTime());
    }

    private boolean isDiffToken(String serverToken, String requestToken) {
        return serverToken == null || !serverToken.equals(requestToken);
    }

    public void logout(String refreshToken) {
        String userEmail = tokenProvider.getSubject(refreshToken);
        redisTemplate.opsForValue().getAndDelete(userEmail);
    }
}
