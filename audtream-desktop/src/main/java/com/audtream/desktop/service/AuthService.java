package com.audtream.desktop.service;

import com.audtream.desktop.config.ApiConfig;
import com.audtream.desktop.model.dto.AuthResponse;
import com.audtream.desktop.model.dto.User;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class AuthService {
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public AuthService() {
        this.client = ApiConfig.getHttpClient();
        this.mapper = ApiConfig.getObjectMapper();
    }

    public AuthResponse login(String username, String password) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/auth/login";

        String json = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                username, password
        );

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                AuthResponse authResponse = mapper.readValue(responseBody, AuthResponse.class);

                TokenStorage.saveToken(authResponse.getToken());

                User user = getCurrentUser();
                if (user != null) {
                    TokenStorage.saveUser(mapper.writeValueAsString(user));
                }

                return authResponse;
            } else {
                throw new IOException("Login failed: " + response.code());
            }
        }
    }

    public AuthResponse register(String username, String email, String password, String role) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/auth/register";

        String json = String.format(
                "{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
                username, email, password, role
        );

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                AuthResponse authResponse = mapper.readValue(responseBody, AuthResponse.class);

                TokenStorage.saveToken(authResponse.getToken());

                User user = getCurrentUser();
                if (user != null) {
                    TokenStorage.saveUser(mapper.writeValueAsString(user));
                }

                return authResponse;
            } else {
                throw new IOException("Registration failed: " + response.code());
            }
        }
    }

    public User getCurrentUser() throws IOException {
        String url = ApiConfig.getBaseUrl() + "/users/me";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return mapper.readValue(responseBody, User.class);
            }
            return null;
        }
    }

    public void logout() {
        TokenStorage.clear();
    }
}