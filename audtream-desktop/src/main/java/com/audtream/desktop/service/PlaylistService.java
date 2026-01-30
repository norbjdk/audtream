package com.audtream.desktop.service;

import com.audtream.desktop.config.ApiConfig;
import com.audtream.desktop.model.dto.PlaylistDTO;
import com.audtream.desktop.model.dto.PlaylistRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import java.io.IOException;
import java.util.List;

public class PlaylistService {
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public PlaylistService() {
        this.client = ApiConfig.getHttpClient();
        this.mapper = ApiConfig.getObjectMapper();
    }

    public PlaylistDTO createPlaylist(PlaylistRequest request) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/playlists";

        String json = mapper.writeValueAsString(request);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request httpRequest = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        System.out.println("Creating playlist: " + request.getName());
        System.out.println("URL: " + url);

        try (Response response = client.newCall(httpRequest).execute()) {
            System.out.println("Response code: " + response.code());

            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                System.out.println("Raw response: " + responseBody);

                // NIE ZAMIENIAJ już spacji na T - custom deserializer obsłuży format ze spacją
                return mapper.readValue(responseBody, PlaylistDTO.class);
            } else {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                System.err.println("Error creating playlist: " + errorBody);
                throw new IOException("Failed to create playlist: " + response.code() + " - " + errorBody);
            }
        }
    }

    public PlaylistDTO getPlaylist(Long playlistId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/playlists/" + playlistId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(response.body().string(), PlaylistDTO.class);
            } else {
                throw new IOException("Failed to fetch playlist: " + response.code());
            }
        }
    }

    public PlaylistDTO updatePlaylist(Long playlistId, PlaylistRequest request) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/playlists/" + playlistId;

        String json = mapper.writeValueAsString(request);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request httpRequest = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(response.body().string(), PlaylistDTO.class);
            } else {
                throw new IOException("Failed to update playlist: " + response.code());
            }
        }
    }

    public void deletePlaylist(Long playlistId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/playlists/" + playlistId;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete playlist: " + response.code());
            }
        }
    }

    public PlaylistDTO addTrackToPlaylist(Long playlistId, Long trackId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/playlists/" + playlistId + "/tracks/" + trackId;

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", null))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(response.body().string(), PlaylistDTO.class);
            } else {
                throw new IOException("Failed to add track to playlist: " + response.code());
            }
        }
    }

    public PlaylistDTO removeTrackFromPlaylist(Long playlistId, Long trackId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/playlists/" + playlistId + "/tracks/" + trackId;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(response.body().string(), PlaylistDTO.class);
            } else {
                throw new IOException("Failed to remove track from playlist: " + response.code());
            }
        }
    }

    public List<PlaylistDTO> getUserPlaylists() throws IOException {
        String url = ApiConfig.getBaseUrl() + "/playlists/user/my";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(
                        response.body().string(),
                        new TypeReference<List<PlaylistDTO>>() {}
                );
            } else {
                throw new IOException("Failed to fetch user playlists: " + response.code());
            }
        }
    }

    public List<PlaylistDTO> getUserPublicPlaylists(Long userId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/playlists/user/" + userId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(
                        response.body().string(),
                        new TypeReference<List<PlaylistDTO>>() {}
                );
            } else {
                throw new IOException("Failed to fetch user playlists: " + response.code());
            }
        }
    }

    public List<PlaylistDTO> getTrendingPlaylists(int limit) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/playlists/explore/trending?limit=" + limit;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(
                        response.body().string(),
                        new TypeReference<List<PlaylistDTO>>() {}
                );
            } else {
                throw new IOException("Failed to fetch trending playlists: " + response.code());
            }
        }
    }

    public List<PlaylistDTO> getNewPlaylists(int limit) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/playlists/explore/new?limit=" + limit;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(
                        response.body().string(),
                        new TypeReference<List<PlaylistDTO>>() {}
                );
            } else {
                throw new IOException("Failed to fetch new playlists: " + response.code());
            }
        }
    }

    public List<PlaylistDTO> getTopPlaylists(int limit) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/playlists/explore/top?limit=" + limit;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(
                        response.body().string(),
                        new TypeReference<List<PlaylistDTO>>() {}
                );
            } else {
                throw new IOException("Failed to fetch top playlists: " + response.code());
            }
        }
    }

    public List<PlaylistDTO> searchPlaylists(String query, int limit) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/playlists/explore/search?query=" +
                java.net.URLEncoder.encode(query, "UTF-8") + "&limit=" + limit;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(
                        response.body().string(),
                        new TypeReference<List<PlaylistDTO>>() {}
                );
            } else {
                throw new IOException("Failed to search playlists: " + response.code());
            }
        }
    }

    public void likePlaylist(Long playlistId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/playlists/" + playlistId + "/like";

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", null))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to like playlist: " + response.code());
            }
        }
    }

    public void incrementPlayCount(Long playlistId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/playlists/" + playlistId + "/play";

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", null))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to increment play count: " + response.code());
            }
        }
    }
}