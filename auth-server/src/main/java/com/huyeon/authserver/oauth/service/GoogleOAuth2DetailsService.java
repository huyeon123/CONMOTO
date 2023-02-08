package com.huyeon.authserver.oauth.service;

import com.huyeon.authserver.auth.entity.Authority;
import com.huyeon.authserver.auth.entity.User;
import com.huyeon.authserver.oauth.exception.OAuth2AuthenticationProcessingException;
import com.huyeon.authserver.oauth.model.OAuth2UserInfo;
import com.huyeon.authserver.oauth.model.OAuth2UserInfoFactory;
import com.huyeon.authserver.oauth.model.UserPrincipal;
import com.huyeon.authserver.auth.repository.AuthRepository;
import com.huyeon.authserver.oauth.model.AuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOAuth2DetailsService extends DefaultOAuth2UserService {
    private final AuthRepository authRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return processOAuth2User(userRequest, oAuth2User);
    }


    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes()
        );

        if (emailNotFound(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        User user;
        Optional<User> userOptional = authRepository.findByEmail(oAuth2UserInfo.getEmail());

        if (userOptional.isPresent()) {
            user = userOptional.get();

            if (areDifferentProvider(user, userRequest)) {
                rejectDifferentProvider(user);
            }

            user = updateExistingUser(user, userRequest, oAuth2UserInfo);
        } else {
            user = signUpOAuth2(userRequest, oAuth2UserInfo);
        }

        return new UserPrincipal(user, oAuth2User.getAttributes());
    }

    private boolean emailNotFound(String email) {
        return StringUtils.isEmpty(email);
    }

    private void rejectDifferentProvider(User user) {
        throw new OAuth2AuthenticationProcessingException(
                user.getProvider() + "로 회원가입 하셨습니다.\n" +
                        user.getProvider() + "로그인으로 진행하시길 바랍니다."
        );
    }

    private boolean areDifferentProvider(User user, OAuth2UserRequest userRequest) {
        return !user.getProvider()
                .equals(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()));
    }

    private User signUpOAuth2(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                .provider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))
                .providerId(oAuth2UserInfo.getId())
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .authorities(Set.of(new Authority(Authority.ROLE_SOCIAL)))
                .build();

        return authRepository.save(user);
    }


    private User updateExistingUser(User existingUser, OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setProvider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()));
        existingUser.setProviderId(oAuth2UserInfo.getId());

        Set<Authority> authorities = existingUser.getAuthorities();
        authorities.add(new Authority(Authority.ROLE_SOCIAL));
        existingUser.setAuthorities(authorities);

        return authRepository.save(existingUser);
    }
}
