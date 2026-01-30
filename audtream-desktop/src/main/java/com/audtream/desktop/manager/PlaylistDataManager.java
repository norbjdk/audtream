package com.audtream.desktop.manager;

import com.audtream.desktop.model.dto.PlaylistDTO;
import com.audtream.desktop.model.dto.PlaylistRequest;
import com.audtream.desktop.model.event.*;
import com.audtream.desktop.service.PlaylistService;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
        PlaylistDTO created = playlistService.createPlaylist(request); // Już zwraca PlaylistDTO
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

        PlaylistDTO playlist = playlistService.getPlaylist(playlistId); // Już zwraca PlaylistDTO
        playlistCache.put(playlistId, playlist);
        return playlist;
    }

    public PlaylistDTO updatePlaylist(Long playlistId, PlaylistRequest request) throws IOException {
        PlaylistDTO updated = playlistService.updatePlaylist(playlistId, request); // Już zwraca PlaylistDTO
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
        PlaylistDTO updated = playlistService.addTrackToPlaylist(playlistId, trackId); // Już zwraca PlaylistDTO
        playlistCache.put(playlistId, updated);
        EventBus.getInstance().publish(new PlaylistTracksChangedEvent(playlistId));
        return updated;
    }

    public PlaylistDTO removeTrack(Long playlistId, Long trackId) throws IOException {
        PlaylistDTO updated = playlistService.removeTrackFromPlaylist(playlistId, trackId); // Już zwraca PlaylistDTO
        playlistCache.put(playlistId, updated);
        EventBus.getInstance().publish(new PlaylistTracksChangedEvent(playlistId));
        return updated;
    }

    public List<PlaylistDTO> getUserPlaylists(boolean forceRefresh) throws IOException {
        long now = System.currentTimeMillis();
        if (!forceRefresh && userPlaylists != null && (now - lastUserPlaylistsFetch) < CACHE_TTL) {
            return new ArrayList<>(userPlaylists);
        }

        userPlaylists = playlistService.getUserPlaylists(); // Już zwraca PlaylistDTO
        lastUserPlaylistsFetch = now;
        userPlaylists.forEach(playlist -> playlistCache.put(playlist.getId(), playlist));
        EventBus.getInstance().publish(new DataRefreshedEvent("user_playlists"));
        return new ArrayList<>(userPlaylists);
    }

    public List<PlaylistDTO> getUserPublicPlaylists(Long userId, boolean forceRefresh) throws IOException {
        List<PlaylistDTO> playlists = playlistService.getUserPublicPlaylists(userId); // Już zwraca PlaylistDTO
        playlists.forEach(playlist -> playlistCache.put(playlist.getId(), playlist));
        return playlists;
    }

    public List<PlaylistDTO> getTrendingPlaylists(int limit, boolean forceRefresh) throws IOException {
        if (!forceRefresh && trendingPlaylists != null) {
            return new ArrayList<>(trendingPlaylists);
        }

        trendingPlaylists = playlistService.getTrendingPlaylists(limit); // Już zwraca PlaylistDTO
        trendingPlaylists.forEach(playlist -> playlistCache.put(playlist.getId(), playlist));
        return new ArrayList<>(trendingPlaylists);
    }

    public List<PlaylistDTO> getNewPlaylists(int limit, boolean forceRefresh) throws IOException {
        if (!forceRefresh && newPlaylists != null) {
            return new ArrayList<>(newPlaylists);
        }

        newPlaylists = playlistService.getNewPlaylists(limit); // Już zwraca PlaylistDTO
        newPlaylists.forEach(playlist -> playlistCache.put(playlist.getId(), playlist));
        return new ArrayList<>(newPlaylists);
    }

    public List<PlaylistDTO> getTopPlaylists(int limit, boolean forceRefresh) throws IOException {
        if (!forceRefresh && topPlaylists != null) {
            return new ArrayList<>(topPlaylists);
        }

        topPlaylists = playlistService.getTopPlaylists(limit); // Już zwraca PlaylistDTO
        topPlaylists.forEach(playlist -> playlistCache.put(playlist.getId(), playlist));
        return new ArrayList<>(topPlaylists);
    }

    public List<PlaylistDTO> searchPlaylists(String query, int limit) throws IOException {
        List<PlaylistDTO> results = playlistService.searchPlaylists(query, limit); // Już zwraca PlaylistDTO
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
}