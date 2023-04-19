package com.huyeon.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L; //30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L; //7일
    private final String secret;
    private Key key;

    /**
     * Bean이 생성되고 생성자에서 yaml 파일에 설정해 놓은 값을 주입 받은 후
     * secret값을 Base64로 Decode해 key변수에 할당 (afterPropertiesSet)
     */
    public TokenProvider(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public UserTokenInfo createToken(Authentication authentication) {
        long now = new Date().getTime();

        String accessToken = createAccessToken((UserDetails) authentication.getPrincipal(), now);
        String refreshToken = createRefreshToken(authentication.getName(), now);

        return UserTokenInfo.builder()
                .accessToken(accessToken)
                .accessTokenExpireTime(ACCESS_TOKEN_EXPIRE_TIME)
                .refreshToken(refreshToken)
                .refreshTokenExpireTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }

    public String createAccessToken(UserDetails userDetails, long now) {
        String authorities = getAuthorities(userDetails);
        Date accessTokenExpiration = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(accessTokenExpiration)
                .compact();
    }

    private String getAuthorities(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private String createRefreshToken(String userEmail, long now) {
        Date refreshTokenExpiration = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(userEmail)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(refreshTokenExpiration)
                .compact();
    }


    public String createAnonymousToken() {
        long now = new Date().getTime();
        Date anonymousTokenExpiration = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject("CONMOTO_ANONYMOUS_TOKEN")
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(anonymousTokenExpiration)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaim(token);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        //Spring Security Core의 User
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private Claims getClaim(String token) {
        return parseClaims(token).getBody();
    }

    public String getSubject(String token) {
        return getClaim(token).getSubject();
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public boolean isExpired(String token) {
        try {
            parseClaims(token);
        } catch (ExpiredJwtException e) {
            return true;
        }
        return false;
    }
}
