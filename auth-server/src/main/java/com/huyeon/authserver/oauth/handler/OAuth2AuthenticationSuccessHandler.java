package com.huyeon.authserver.oauth.handler;

import com.huyeon.authserver.auth.dto.UserTokenInfo;
import com.huyeon.authserver.oauth.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.huyeon.authserver.utils.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        UserTokenInfo tokenInfo = tokenProvider.createToken(authentication);

        saveRedis(authentication.getName(), tokenInfo.getRefreshToken());

        return UriComponentsBuilder.fromUriString("http://localhost:8000/auth/oauth2/success")
                .queryParam("accessToken", tokenInfo.getAccessToken())
                .queryParam("accessTokenExpireTime", tokenInfo.getAccessTokenExpireTime())
                .queryParam("refreshToken", tokenInfo.getRefreshToken())
                .queryParam("refreshTokenExpireTime", tokenInfo.getRefreshTokenExpireTime())
                .build().toUriString();
    }

    private void saveRedis(String email, String refreshToken) {
        redisTemplate.opsForValue()
                .set("RefreshToken:" + email, refreshToken, 7, TimeUnit.DAYS);
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
