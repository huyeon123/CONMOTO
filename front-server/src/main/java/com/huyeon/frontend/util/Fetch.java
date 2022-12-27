package com.huyeon.frontend.util;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@Component
public class Fetch {
    private static final String API_SERVER_ADDR = "http://localhost:8000/api";

    public void bindResponse(String url, String accessToken, Model model) {
        Map<String, Object> response = get(url, accessToken);
        model.addAllAttributes(response);
    }

    public Map<String, Object> get(String url, String accessToken) {
        return fetch(url, HttpMethod.GET, accessToken);
    }

    private Map<String, Object> fetch(String url, HttpMethod method, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(
                routeApiServer(url),
                method,
                getRequestHeader(accessToken),
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        ).getBody();
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

    private void addAllAttributes(Model model, Map<String, Object> body) {
        if (body != null) {
            model.addAllAttributes(body);
        }
    }

    public boolean hasNoPermission(Map<String, Object> response) {
        String status = (String) response.get("status");
        return status != null && status.startsWith("fail:");
    }
}
