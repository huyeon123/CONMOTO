package com.huyeon.frontend.util;

import com.huyeon.frontend.controller.error.ErrorHandler;
import com.huyeon.frontend.exception.NotFoundException;
import com.huyeon.frontend.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Component
public class Fetch {
    @Value("${conmoto.server.api}")
    private String API_SERVER_ADDR;

    public void bindResponse(String url, String accessToken, Model model) {
        Map<String, Object> response = get(url, accessToken);
        model.addAllAttributes(response);
    }

    public Map<String, Object> get(String url, String accessToken) {
        return fetch(url, HttpMethod.GET, accessToken);
    }

    private Map<String, Object> fetch(String url, HttpMethod method, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        if (accessToken == null) {
            throw new UnauthorizedException("accessToken 없음");
        }

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                routeApiServer(url),
                method,
                getRequestHeader(accessToken),
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );

        ErrorHandler.checkError(response.getStatusCode());
        checkFail(Objects.requireNonNull(response.getBody()));
        return response.getBody();
    }

    private void checkFail(Map<String, Object> body) {
        String status = (String) body.get("status");
        if (status != null && status.startsWith("fail:")) throw new NotFoundException("STATUS CODE: " + HttpStatus.NOT_FOUND.value());
    }


    private String routeApiServer(String path) {
        return API_SERVER_ADDR + path;
    }

    private HttpEntity<?> getRequestHeader(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        return new HttpEntity<>(httpHeaders);
    }

    public boolean hasNoPermission(Map<String, Object> response) {
        String status = (String) response.get("status");
        return status != null && status.startsWith("fail:");
    }
}
