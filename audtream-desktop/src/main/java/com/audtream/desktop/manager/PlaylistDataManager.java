package com.audtream.desktop.manager;

import com.audtream.desktop.model.dto.PlaylistDTO;
import com.audtream.desktop.model.dto.PlaylistRequest;
import com.audtream.desktop.model.dto.TrackDTO;
import com.audtream.desktop.model.event.*;
import com.audtream.desktop.service.PlaylistService;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlaylistDataManager {
    private static PlaylistDataManager instance;
    private final PlaylistService playlistService;
    private final Map<Long, PlaylistDTO> playlistCache;
    private List<PlaylistDTO> userPlaylists;
    private List<PlaylistDTO> trendingPlaylists;
    private List<PlaylistDTO> newPlaylists;
    private List<PlaylistDTO> topPlaylists;
    private long lastUserPlaylistsFetch;
    private static final long CACHE_TTL = 300000;

    private PlaylistDataManager() {
        this.playlistService = new PlaylistService();
        this.playlistCache = new ConcurrentHashMap<>();
        this.lastUserPlaylistsFetch = 0;
    }

    public static PlaylistDataManager getInstance() {
        if (instance == null) {
            instance = new PlaylistDataManager();
        }
        return instance;
    }

    public PlaylistDTO createPlaylist(PlaylistRequest request) throws IOException {
        PlaylistDTO created = convertToPlaylistDTO(playlistService.createPlaylist(request));
        playlistCache.put(created.getId(), created);
        if (userPlaylists != null) {
            userPlaylists.add(created);
        }
        EventBus.getInstance().publish(new PlaylistCreatedEvent(created.getId()));
        return created;
    }

    public PlaylistDTO getPlaylist(Long playlistId, boolean useCache) throws IOException {
        if (useCache && playlistCache.containsKey(playlistId)) {
            return playlistCache.get(playlistId);
        }

        PlaylistDTO playlist = convertToPlaylistDTO(playlistService.getPlaylist(playlistId));
        playlistCache.put(playlistId, playlist);
        return playlist;
    }

    public PlaylistDTO updatePlaylist(Long playlistId, PlaylistRequest request) throws IOException {
        PlaylistDTO updated = convertToPlaylistDTO(playlistService.updatePlaylist(playlistId, request));
        playlistCache.put(playlistId, updated);
        if (userPlaylists != null) {
            userPlaylists.removeIf(p -> p.getId().equals(playlistId));
            userPlaylists.add(updated);
        }
        EventBus.getInstance().publish(new PlaylistUpdatedEvent(playlistId));
        return updated;
    }

    public void deletePlaylist(Long playlistId) throws IOException {
        playlistService.deletePlaylist(playlistId);
        playlistCache.remove(playlistId);
        if (userPlaylists != null) {
            userPlaylists.removeIf(p -> p.getId().equals(playlistId));
        }
        EventBus.getInstance().publish(new PlaylistDeletedEvent(playlistId));
    }

    public PlaylistDTO addTrack(Long playlistId, Long trackId) throws IOException {
        PlaylistDTO updated = convertToPlaylistDTO(playlistService.addTrackToPlaylist(playlistId, trackId));
        playlistCache.put(playlistId, updated);
        EventBus.getInstance().publish(new PlaylistTracksChangedEvent(playlistId));
        return updated;
    }

    public PlaylistDTO removeTrack(Long playlistId, Long trackId) throws IOException {
        PlaylistDTO updated = convertToPlaylistDTO(playlistService.removeTrackFromPlaylist(playlistId, trackId));
        playlistCache.put(playlistId, updated);
        EventBus.getInstance().publish(new PlaylistTracksChangedEvent(playlistId));
        return updated;
    }

    public List<PlaylistDTO> getUserPlaylists(boolean forceRefresh) throws IOException {
        long now = System.currentTimeMillis();
        if (!forceRefresh && userPlaylists != null && (now - lastUserPlaylistsFetch) < CACHE_TTL) {
            return new ArrayList<>(userPlaylists);
        }

        userPlaylists = convertToPlaylistDTO(playlistService.getUserPlaylists());
        lastUserPlaylistsFetch = now;
        userPlaylists.forEach(playlist -> playlistCache.put(playlist.getId(), playlist));
        EventBus.getInstance().publish(new DataRefreshedEvent("user_playlists"));
        return new ArrayList<>(userPlaylists);
    }

    public List<PlaylistDTO> getUserPublicPlaylists(Long userId, boolean forceRefresh) throws IOException {
        List<PlaylistDTO> playlists = convertToPlaylistDTO(playlistService.getUserPublicPlaylists(userId));
        playlists.forEach(playlist -> playlistCache.put(playlist.getId(), playlist));
        return playlists;
    }

    public List<PlaylistDTO> getTrendingPlaylists(int limit, boolean forceRefresh) throws IOException {
        if (!forceRefresh && trendingPlaylists != null) {
            return new ArrayList<>(trendingPlaylists);
        }

        trendingPlaylists = convertToPlaylistDTO(playlistService.getTrendingPlaylists(limit));
        trendingPlaylists.forEach(playlist -> playlistCache.put(playlist.getId(), playlist));
        return new ArrayList<>(trendingPlaylists);
    }

    public List<PlaylistDTO> getNewPlaylists(int limit, boolean forceRefresh) throws IOException {
        if (!forceRefresh && newPlaylists != null) {
            return new ArrayList<>(newPlaylists);
        }

        newPlaylists = convertToPlaylistDTO(playlistService.getNewPlaylists(limit));
        newPlaylists.forEach(playlist -> playlistCache.put(playlist.getId(), playlist));
        return new ArrayList<>(newPlaylists);
    }

    public List<PlaylistDTO> getTopPlaylists(int limit, boolean forceRefresh) throws IOException {
        if (!forceRefresh && topPlaylists != null) {
            return new ArrayList<>(topPlaylists);
        }

        topPlaylists = convertToPlaylistDTO(playlistService.getTopPlaylists(limit));
        topPlaylists.forEach(playlist -> playlistCache.put(playlist.getId(), playlist));
        return new ArrayList<>(topPlaylists);
    }

    public List<PlaylistDTO> searchPlaylists(String query, int limit) throws IOException {
        List<PlaylistDTO> results = convertToPlaylistDTO(playlistService.searchPlaylists(query, limit));
        results.forEach(playlist -> playlistCache.put(playlist.getId(), playlist));
        return results;
    }

    public void likePlaylist(Long playlistId) throws IOException {
        playlistService.likePlaylist(playlistId);
        PlaylistDTO playlist = playlistCache.get(playlistId);
        if (playlist != null) {
            playlist.setLikes(playlist.getLikes() + 1);
        }
        EventBus.getInstance().publish(new PlaylistLikedEvent(playlistId));
    }

    public void incrementPlayCount(Long playlistId) throws IOException {
        playlistService.incrementPlayCount(playlistId);
        PlaylistDTO playlist = playlistCache.get(playlistId);
        if (playlist != null) {
            playlist.setPlays(playlist.getPlays() + 1);
        }
    }

    public void clearCache() {
        playlistCache.clear();
        userPlaylists = null;
        trendingPlaylists = null;
        newPlaylists = null;
        topPlaylists = null;
        lastUserPlaylistsFetch = 0;
    }

    // KONWERSJA Z PlaylistResponse NA PlaylistDTO
    private PlaylistDTO convertToPlaylistDTO(com.audtream.desktop.model.dto.PlaylistResponse response) {
        if (response == null) return null;

        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(response.getId());
        dto.setName(response.getName());
        dto.setDescription(response.getDescription());
        dto.setIsPublic(response.getIsPublic());
        dto.setCoverImageUrl(response.getCoverImageUrl());
        dto.setTrackCount(response.getTrackCount());
        dto.setTotalDuration(response.getTotalDuration());
        dto.setPlays(response.getPlays());
        dto.setLikes(response.getLikes());
        dto.setCreatedAt(response.getCreatedAt());
        dto.setUpdatedAt(response.getUpdatedAt());
        dto.setUserId(response.getUserId());
        dto.setUsername(response.getUsername());

        // Konwersja tracków jeśli istnieją
        if (response.getTracks() != null) {
            // Musisz mieć metodę convertToTrackDTO w TrackDataManager lub osobny konwerter
            // dto.setTracks(convertTracks(response.getTracks()));
        }

        return dto;
    }

    private List<PlaylistDTO> convertToPlaylistDTO(List<com.audtream.desktop.model.dto.PlaylistResponse> responses) {
        if (responses == null) return new ArrayList<>();
        return responses.stream()
                .map(this::convertToPlaylistDTO)
                .collect(Collectors.toList());
    }

    // Opcjonalnie: jeśli potrzebujesz konwersji tracków
    private List<TrackDTO> convertTracks(List<com.audtream.desktop.model.dto.TrackResponse> trackResponses) {
        if (trackResponses == null) return new ArrayList<>();

        // Tu użyj konwertera z TrackDataManager lub utwórz własny
        // Na przykład:
        // return trackResponses.stream()
        //         .map(trackResponse -> {
        //             TrackDTO trackDTO = new TrackDTO();
        //             trackDTO.setId(trackResponse.getId());
        //             trackDTO.setTitle(trackResponse.getTitle());
        //             // ... i tak dalej
        //             return trackDTO;
        //         })
        //         .collect(Collectors.toList());

        return new ArrayList<>(); // placeholder
    }
}