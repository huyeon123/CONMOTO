package com.huyeon.authserver.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserTokenInfo {
    private String accessToken;
    private long accessTokenExpireTime;
    private String refreshToken;
    private long refreshTokenExpireTime;
}
