package com.audtream.desktop.service;

import com.audtream.desktop.config.ApiConfig;
import com.audtream.desktop.model.dto.UserStatsDTO;
import com.audtream.desktop.model.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import java.io.IOException;
import java.util.Map;

public class UserService {
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public UserService() {
        this.client = ApiConfig.getHttpClient();
        this.mapper = ApiConfig.getObjectMapper();
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
            } else {
                throw new IOException("Failed to fetch current user: " + response.code());
            }
        }
    }

    public User getUserByUsername(String username) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/users/" + username;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return mapper.readValue(responseBody, User.class);
            } else {
                throw new IOException("Failed to fetch user: " + response.code());
            }
        }
    }

    public UserStatsDTO getArtistStats() throws IOException {
        String url = ApiConfig.getBaseUrl() + "/users/dashboard/stats";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                Map<String, Object> statsMap = mapper.readValue(responseBody, Map.class);

                UserStatsDTO stats = new UserStatsDTO();
                stats.setTotalTracks((Integer) statsMap.get("totalTracks"));
                stats.setTotalPlays((Integer) statsMap.get("totalPlays"));
                stats.setTotalLikes((Integer) statsMap.get("totalLikes"));
                stats.setTopGenre((String) statsMap.get("topGenre"));
                stats.setAverageDuration((Integer) statsMap.get("averageDuration"));
                stats.setMostPlayedTrack((String) statsMap.get("mostPlayedTrack"));
                stats.setMostPlayedTrackPlays((Integer) statsMap.get("mostPlayedTrackPlays"));

                return stats;
            } else {
                throw new IOException("Failed to fetch artist stats: " + response.code());
            }
        }
    }
}