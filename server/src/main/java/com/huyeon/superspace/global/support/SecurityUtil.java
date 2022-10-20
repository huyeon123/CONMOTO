package com.huyeon.superspace.global.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Slf4j
public class SecurityUtil {

    public static Optional<String> getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("Security Context에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails principal = (UserDetails) authentication.getPrincipal();
            String username = principal.getUsername();
            return Optional.ofNullable(username);
        }

        if (authentication.getPrincipal() instanceof String) {
            String username = (String) authentication.getPrincipal();
            return Optional.ofNullable(username);
        }

        return Optional.empty();
    }
}