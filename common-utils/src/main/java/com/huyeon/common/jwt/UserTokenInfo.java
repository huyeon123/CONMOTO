package com.huyeon.common.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserTokenInfo {
    private String accessToken;
    private long accessTokenExpireTime;
    private String refreshToken;
    private long refreshTokenExpireTime;
}
