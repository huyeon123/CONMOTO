package com.huyeon.superspace.domain.auth.service;

import com.huyeon.superspace.domain.auth.repository.AuthRepository;
import com.huyeon.superspace.global.model.UserDetailsImpl;
import com.huyeon.superspace.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * AuthenticationManger를 직접 정의하지 않고, AuthenticationManagerFactoryBean을 통해 주입받았기 때문에
 * 이 AuthenticationManger는 DaoAuthenticationProvider를 Default AuthenticationProvider로 설정한다.
 * <p>
 * DaoAuthenticationProvider는 반드시 1개의 UserDetailsService를 발견할 수 있어야한다.
 * 만약 없다면, InmemoryUserDetailsManager에 사용자가 등록되어 제공된다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = authRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 사용자입니다."));

        return new UserDetailsImpl(user);
    }
}
