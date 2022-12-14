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
            try {
                String token = resolveAuthToken(exchange);
                if (tokenExtractor.validateToken(token)) {
                    String email = tokenExtractor.getSubject(token);

                    addAuthHeaders(exchange.getRequest(), email);

                    return chain.filter(exchange);
                }
            } catch (NullPointerException e) {
                log.info("JWT 토큰을 획득할 수 없습니다.");
            }
            return onError(exchange, "JWT 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
        };
    }

    private String resolveAuthToken(ServerWebExchange exchange) throws NullPointerException{
        return exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)
                .get(0).substring(7);
    }


    private void addAuthHeaders(ServerHttpRequest request, String email) {
        request.mutate()
                .header("X-Authorization-Id", email)
                .build();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(httpStatus);

        log.error(err);

        return response.setComplete();
    }

    static class Config {

    }
}
