package com.audtream.desktop.service;

import com.audtream.desktop.config.ApiConfig;
import com.audtream.desktop.model.dto.User;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CurrentUserService {
    private static User currentUser;
    private static final ObjectMapper mapper = ApiConfig.getObjectMapper();

    public static void setCurrentUser(User user) {
        currentUser = user;
        try {
            String userJson = mapper.writeValueAsString(user);
            TokenStorage.saveUser(userJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static User getCurrentUser() {
        if (currentUser == null) {
            String userJson = TokenStorage.getUser();
            if (userJson != null && !userJson.isEmpty()) {
                try {
                    currentUser = mapper.readValue(userJson, User.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return TokenStorage.isLoggedIn() && getCurrentUser() != null;
    }

    public static void logout() {
        currentUser = null;
        TokenStorage.clear();
    }

    public static String getUsername() {
        User user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    public static Long getUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
}
