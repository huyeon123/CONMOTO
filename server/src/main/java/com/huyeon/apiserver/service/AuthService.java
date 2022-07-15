package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.UserSignInReq;
import com.huyeon.apiserver.model.dto.UserSignUpReq;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    //회원가입
    public boolean signUp(UserSignUpReq request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            User user = new User(request);
            user.encryptPassword(passwordEncoder);
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void signIn(UserSignInReq request) throws Exception{
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
