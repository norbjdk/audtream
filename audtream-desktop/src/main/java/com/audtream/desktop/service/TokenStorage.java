package com.audtream.desktop.service;

import java.util.prefs.Preferences;

public class TokenStorage {
    private static final Preferences prefs = Preferences.userNodeForPackage(TokenStorage.class);
    private static final String TOKEN_KEY = "auth_token";
    private static final String USER_KEY = "user_data";

    public static void saveToken(String token) {
        prefs.put(TOKEN_KEY, token);
    }

    public static String getToken() {
        return prefs.get(TOKEN_KEY, null);
    }

    public static void saveUser(String userJson) {
        prefs.put(USER_KEY, userJson);
    }

    public static String getUser() {
        return prefs.get(USER_KEY, null);
    }

    public static void clear() {
        prefs.remove(TOKEN_KEY);
        prefs.remove(USER_KEY);
        System.out.println("token cleared");
    }

    public static boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }
}
