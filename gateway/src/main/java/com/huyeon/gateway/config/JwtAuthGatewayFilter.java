package com.huyeon.gateway.config;

import com.huyeon.gateway.jwt.TokenExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthGatewayFilter extends AbstractGatewayFilterFactory<JwtAuthGatewayFilter.Config> {

    private final TokenExtractor tokenExtractor;

    public JwtAuthGatewayFilter(TokenExtractor tokenExtractor) {
        super(Config.class);
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String accessToken = resolveAccessToken(exchange);

            if (isValidToken(accessToken)) {
                attachAuthId(exchange, accessToken);
            } else {
                return onError(exchange, "유효하지 않는 JWT 입니다.", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    private boolean isValidToken(String token) {
        return token != null && tokenExtractor.validateToken(token);
    }

    private String resolveAccessToken(ServerWebExchange exchange) {
        try {
            return exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)
                    .get(0).substring(7);
        } catch (NullPointerException e) {
            log.info("JWT를 획득할 수 없습니다.");
            return null;
        }
    }

    private void attachAuthId(
            ServerWebExchange exchange,
            String token) {
        String email = tokenExtractor.getSubject(token);
        addAuthHeaders(exchange.getRequest(), email);
    }


    private void addAuthHeaders(ServerHttpRequest request, String email) {
        request.mutate()
                .header("X-Authorization-Id", email)
                .build();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String msg, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        log.info(msg);
        return response.setComplete();
    }

    static class Config {

    }
}
