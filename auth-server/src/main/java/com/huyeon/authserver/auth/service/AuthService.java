package com.huyeon.authserver.auth.service;

import com.huyeon.authserver.auth.dto.TempLoginReqDTO;
import com.huyeon.authserver.auth.dto.UserSignInReq;
import com.huyeon.authserver.auth.dto.UserSignUpReq;
import com.huyeon.authserver.auth.dto.UserTokenInfo;
import com.huyeon.authserver.auth.entity.User;
import com.huyeon.authserver.auth.exception.DuplicateEmailException;
import com.huyeon.authserver.auth.exception.IncorrectLoginCodeException;
import com.huyeon.authserver.auth.exception.NonMembersException;
import com.huyeon.authserver.auth.repository.AuthRepository;
import com.huyeon.authserver.oauth.model.AuthProvider;
import com.huyeon.authserver.utils.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
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

    public boolean isDuplicateEmail(String email) {
        return authRepository.existsByEmail(email);
    }

    public boolean isSocialAccount(String email) {
        Optional<User> optionalUser = findByEmail(email);
        AuthProvider provider = optionalUser
                .orElseThrow(NonMembersException::new)
                .getProvider();
        return Objects.nonNull(provider);
    }

    @Transactional
    public UserTokenInfo logIn(UserSignInReq request) {
        try {
            Authentication authentication = authenticate(request.getEmail(), request.getPassword());
            return generateToken(authentication, request.getEmail());
        } catch (BadCredentialsException e) {
            String message = "아이디/비밀번호가 일치하지 않습니다.";
            throw new BadCredentialsException(message);
        } catch (RedisConnectionFailureException e) {
            String message = "Redis가 shutdown 상태입니다!! 토큰을 저장하기 위해 Redis를 활성화하십시오.";
            throw new RedisConnectionFailureException(message);
        }
    }

    private Authentication authenticate(String email, String password) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(email, password);

        return authenticationManagerBuilder.getObject().authenticate(token);
    }

    private UserTokenInfo generateToken(Authentication authentication, String email) {
        UserTokenInfo token = tokenProvider.createToken(authentication);
        saveRefreshToken(email, token.getRefreshToken());
        return token;
    }

    @Transactional
    public String generateNewAccessToken(String refreshToken) {
        String userEmail = tokenProvider.getSubject(refreshToken);

        String serverToken = getTargetRefreshToken(userEmail);

        if (isDiffToken(serverToken, refreshToken)) {
            log.warn("토큰 탈취 의심 - Redis에 저장된 refreshToken을 삭제합니다.");
            deleteRefreshToken(userEmail);
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
        deleteRefreshToken(userEmail);
    }

    @Transactional
    public UserTokenInfo logInTemporally(TempLoginReqDTO request) {
        String targetCode = getTargetLoginCode(request.getEmail());

        if (areDifferent(targetCode, request.getLoginCode())) {
            throw new IncorrectLoginCodeException("올바르지 않은 로그인 코드입니다.");
        }

        Optional<User> optionalUser = findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) throw new NonMembersException();

        User user = optionalUser.get();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Authentication authentication =
                new AnonymousAuthenticationToken("temporally", userDetails, userDetails.getAuthorities());

        return generateToken(authentication, user.getEmail());
    }

    private boolean areDifferent(String targetCode, String requestCode) {
        return !StringUtils.equals(targetCode, requestCode);
    }

    private String getTargetLoginCode(String email) {
        return redisTemplate.opsForValue().get("loginCode:" + email);
    }

    private String getTargetRefreshToken(String email) {
        return redisTemplate.opsForValue().get("RefreshToken:" + email);
    }

    private void saveRefreshToken(String email, String refreshToken) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("RefreshToken:" + email, refreshToken, 7, TimeUnit.DAYS);
    }

    private void deleteRefreshToken(String email) {
        redisTemplate.opsForValue().getAndDelete("RefreshToken:" + email);
    }

    private Optional<User> findByEmail(String email) {
        return authRepository.findByEmail(email);
    }

    public String getAnonymousToken() {
        return tokenProvider.createAnonymousToken();
    }
}
