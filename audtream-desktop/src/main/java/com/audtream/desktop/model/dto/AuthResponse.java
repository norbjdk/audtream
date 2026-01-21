package com.audtream.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponse {
    @JsonProperty("token")
    private String token;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private String role;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
