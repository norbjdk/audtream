package com.audtream.desktop.service;

import com.audtream.desktop.config.ApiConfig;
import com.audtream.desktop.model.dto.PlaylistRequest;
import com.audtream.desktop.model.dto.PlaylistResponse;
import com.audtream.desktop.model.dto.TrackResponse;
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

    public PlaylistResponse createPlaylist(PlaylistRequest request) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/api/playlists";

        String json = mapper.writeValueAsString(request);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request httpRequest = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(response.body().string(), PlaylistResponse.class);
            } else {
                throw new IOException("Failed to create playlist: " + response.code());
            }
        }
    }

    public PlaylistResponse getPlaylist(Long playlistId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/api/playlists/" + playlistId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(response.body().string(), PlaylistResponse.class);
            } else {
                throw new IOException("Failed to fetch playlist: " + response.code());
            }
        }
    }

    public PlaylistResponse updatePlaylist(Long playlistId, PlaylistRequest request) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/api/playlists/" + playlistId;

        String json = mapper.writeValueAsString(request);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request httpRequest = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(response.body().string(), PlaylistResponse.class);
            } else {
                throw new IOException("Failed to update playlist: " + response.code());
            }
        }
    }

    public void deletePlaylist(Long playlistId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/api/playlists/" + playlistId;

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

    // ========== OPERACJE NA UTWORACH ==========

    public PlaylistResponse addTrackToPlaylist(Long playlistId, Long trackId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/api/playlists/" + playlistId + "/tracks/" + trackId;

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", null))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(response.body().string(), PlaylistResponse.class);
            } else {
                throw new IOException("Failed to add track to playlist: " + response.code());
            }
        }
    }

    public PlaylistResponse removeTrackFromPlaylist(Long playlistId, Long trackId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/api/playlists/" + playlistId + "/tracks/" + trackId;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(response.body().string(), PlaylistResponse.class);
            } else {
                throw new IOException("Failed to remove track from playlist: " + response.code());
            }
        }
    }

    // ========== ENDPOINTY UŻYTKOWNIKA ==========

    public List<PlaylistResponse> getUserPlaylists() throws IOException {
        String url = ApiConfig.getBaseUrl() + "/api/playlists/user/my";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(
                        response.body().string(),
                        new TypeReference<List<PlaylistResponse>>() {}
                );
            } else {
                throw new IOException("Failed to fetch user playlists: " + response.code());
            }
        }
    }

    public List<PlaylistResponse> getUserPublicPlaylists(Long userId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/api/playlists/user/" + userId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(
                        response.body().string(),
                        new TypeReference<List<PlaylistResponse>>() {}
                );
            } else {
                throw new IOException("Failed to fetch user playlists: " + response.code());
            }
        }
    }

    // ========== EKSPLORACJA ==========

    public List<PlaylistResponse> getTrendingPlaylists(int limit) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/api/playlists/explore/trending?limit=" + limit;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(
                        response.body().string(),
                        new TypeReference<List<PlaylistResponse>>() {}
                );
            } else {
                throw new IOException("Failed to fetch trending playlists: " + response.code());
            }
        }
    }

    public List<PlaylistResponse> getNewPlaylists(int limit) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/api/playlists/explore/new?limit=" + limit;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(
                        response.body().string(),
                        new TypeReference<List<PlaylistResponse>>() {}
                );
            } else {
                throw new IOException("Failed to fetch new playlists: " + response.code());
            }
        }
    }

    public List<PlaylistResponse> getTopPlaylists(int limit) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/api/playlists/explore/top?limit=" + limit;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(
                        response.body().string(),
                        new TypeReference<List<PlaylistResponse>>() {}
                );
            } else {
                throw new IOException("Failed to fetch top playlists: " + response.code());
            }
        }
    }

    public List<PlaylistResponse> searchPlaylists(String query, int limit) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/api/playlists/explore/search?query=" +
                java.net.URLEncoder.encode(query, "UTF-8") + "&limit=" + limit;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(
                        response.body().string(),
                        new TypeReference<List<PlaylistResponse>>() {}
                );
            } else {
                throw new IOException("Failed to search playlists: " + response.code());
            }
        }
    }

    // ========== OPERACJE SPOŁECZNOŚCIOWE ==========

    public void likePlaylist(Long playlistId) throws IOException {
        String url = ApiConfig.getBaseUrl() + "/api/playlists/" + playlistId + "/like";

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
        String url = ApiConfig.getBaseUrl() + "/api/playlists/" + playlistId + "/play";

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