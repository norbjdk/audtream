package com.audtream.desktop.service;

import com.audtream.desktop.config.ApiConfig;
import com.audtream.desktop.model.dto.TrackResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrackService {
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public TrackService() {
        this.client = ApiConfig.getHttpClient();
        this.mapper = ApiConfig.getObjectMapper();
    }

    public List<TrackResponse> getUserTracks() throws IOException {
        String url = ApiConfig.getBaseUrl() + "/tracks";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return mapper.readValue(responseBody, new TypeReference<List<TrackResponse>>() {});
            } else {
                throw new IOException("Failed to fetch tracks: " + response.code());
            }
        }
    }

    public List<TrackResponse> getAllTracks(String genre, String sort) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(ApiConfig.getBaseUrl() + "/tracks/all").newBuilder();

        if (genre != null && !genre.isEmpty() && !genre.equalsIgnoreCase("all")) {
            urlBuilder.addQueryParameter("genre", genre);
        }
        if (sort != null && !sort.isEmpty()) {
            urlBuilder.addQueryParameter("sort", sort);
        }

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return mapper.readValue(responseBody, new TypeReference<List<TrackResponse>>() {});
            } else {
                throw new IOException("Failed to fetch all tracks: " + response.code());
            }
        }
    }

    public List<TrackResponse> getRecommendedTracks(int limit) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/tracks/recommended?limit=" + limit;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return mapper.readValue(responseBody, new TypeReference<List<TrackResponse>>() {});
            } else {
                throw new IOException("Failed to fetch recommended tracks: " + response.code());
            }
        }
    }

    public List<TrackResponse> getNewReleases(int limit) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/tracks/new-releases?limit=" + limit;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return mapper.readValue(responseBody, new TypeReference<List<TrackResponse>>() {});
            } else {
                throw new IOException("Failed to fetch new releases: " + response.code());
            }
        }
    }

    public List<TrackResponse> getTopTracks(int limit) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/tracks/top?limit=" + limit;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return mapper.readValue(responseBody, new TypeReference<List<TrackResponse>>() {});
            } else {
                throw new IOException("Failed to fetch top tracks: " + response.code());
            }
        }
    }

    public TrackResponse getTrackById(Long trackId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/tracks/" + trackId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return mapper.readValue(responseBody, TrackResponse.class);
            } else {
                throw new IOException("Failed to fetch track: " + response.code());
            }
        }
    }

    public String getTrackStreamUrl(Long trackId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/tracks/" + trackId + "/url";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string().replace("\"", ""); // Usuń cudzysłowy
            } else {
                throw new IOException("Failed to get stream URL: " + response.code());
            }
        }
    }

    public void incrementPlayCount(Long trackId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/tracks/" + trackId + "/play";

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", null))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Failed to increment play count: " + response.code());
            }
        }
    }

    public void toggleLike(Long trackId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/tracks/" + trackId + "/like";

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", null))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Failed to toggle like: " + response.code());
            }
        }
    }

    public void deleteTrack(Long trackId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/tracks/" + trackId;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete track: " + response.code());
            }
        }
    }
}