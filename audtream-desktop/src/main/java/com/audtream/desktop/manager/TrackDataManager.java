package com.audtream.desktop.manager;

import com.audtream.desktop.model.dto.TrackDTO;
import com.audtream.desktop.model.event.*;
import com.audtream.desktop.service.TrackService;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TrackDataManager {
    private static TrackDataManager instance;
    private final TrackService trackService;
    private final Map<Long, TrackDTO> trackCache;
    private List<TrackDTO> userTracks;
    private List<TrackDTO> allTracks;
    private List<TrackDTO> recommendedTracks;
    private List<TrackDTO> newReleases;
    private List<TrackDTO> topTracks;
    private long lastUserTracksFetch;
    private long lastAllTracksFetch;
    private static final long CACHE_TTL = 300000;

    private TrackDataManager() {
        this.trackService = new TrackService();
        this.trackCache = new ConcurrentHashMap<>();
        this.lastUserTracksFetch = 0;
        this.lastAllTracksFetch = 0;
    }

    public static TrackDataManager getInstance() {
        if (instance == null) {
            instance = new TrackDataManager();
        }
        return instance;
    }

    public List<TrackDTO> getUserTracks(boolean forceRefresh) throws IOException {
        long now = System.currentTimeMillis();
        if (!forceRefresh && userTracks != null && (now - lastUserTracksFetch) < CACHE_TTL) {
            return new ArrayList<>(userTracks);
        }

        userTracks = convertToTrackDTO(trackService.getUserTracks());
        lastUserTracksFetch = now;
        userTracks.forEach(track -> trackCache.put(track.getId(), track));
        EventBus.getInstance().publish(new DataRefreshedEvent("user_tracks"));
        return new ArrayList<>(userTracks);
    }

    public List<TrackDTO> getAllTracks(String genre, String sort, boolean forceRefresh) throws IOException {
        long now = System.currentTimeMillis();
        if (!forceRefresh && allTracks != null && (now - lastAllTracksFetch) < CACHE_TTL) {
            return filterAndSort(allTracks, genre, sort);
        }

        allTracks = convertToTrackDTO(trackService.getAllTracks(genre, sort));
        lastAllTracksFetch = now;
        allTracks.forEach(track -> trackCache.put(track.getId(), track));
        EventBus.getInstance().publish(new DataRefreshedEvent("all_tracks"));
        return new ArrayList<>(allTracks);
    }

    public List<TrackDTO> getRecommendedTracks(int limit, boolean forceRefresh) throws IOException {
        if (!forceRefresh && recommendedTracks != null) {
            return new ArrayList<>(recommendedTracks);
        }

        recommendedTracks = convertToTrackDTO(trackService.getRecommendedTracks(limit));
        recommendedTracks.forEach(track -> trackCache.put(track.getId(), track));
        return new ArrayList<>(recommendedTracks);
    }

    public List<TrackDTO> getNewReleases(int limit, boolean forceRefresh) throws IOException {
        if (!forceRefresh && newReleases != null) {
            return new ArrayList<>(newReleases);
        }

        newReleases = convertToTrackDTO(trackService.getNewReleases(limit));
        newReleases.forEach(track -> trackCache.put(track.getId(), track));
        return new ArrayList<>(newReleases);
    }

    public List<TrackDTO> getTopTracks(int limit, boolean forceRefresh) throws IOException {
        if (!forceRefresh && topTracks != null) {
            return new ArrayList<>(topTracks);
        }

        topTracks = convertToTrackDTO(trackService.getTopTracks(limit));
        topTracks.forEach(track -> trackCache.put(track.getId(), track));
        return new ArrayList<>(topTracks);
    }

    public TrackDTO getTrackById(Long trackId, boolean useCache) throws IOException {
        if (useCache && trackCache.containsKey(trackId)) {
            return trackCache.get(trackId);
        }

        TrackDTO track = convertToTrackDTO(trackService.getTrackById(trackId));
        trackCache.put(trackId, track);
        return track;
    }

    public String getStreamUrl(Long trackId) throws IOException {
        return trackService.getTrackStreamUrl(trackId);
    }

    public void incrementPlayCount(Long trackId) throws IOException {
        trackService.incrementPlayCount(trackId);
        TrackDTO track = trackCache.get(trackId);
        if (track != null) {
            track.setPlays(track.getPlays() + 1);
        }
        EventBus.getInstance().publish(new TrackPlayedEvent(trackId));
    }

    public void toggleLike(Long trackId) throws IOException {
        trackService.toggleLike(trackId);
        TrackDTO track = trackCache.get(trackId);
        if (track != null) {
            // Zakładając, że API zwraca zaktualizowany stan like
            // Możesz chcieć dodać pole 'liked' w TrackDTO jeśli API to obsługuje
            track.setLikes(track.getLikes() + 1);
        }
        EventBus.getInstance().publish(new TrackLikedEvent(trackId));
    }

    public void deleteTrack(Long trackId) throws IOException {
        trackService.deleteTrack(trackId);
        trackCache.remove(trackId);
        if (userTracks != null) {
            userTracks.removeIf(t -> t.getId().equals(trackId));
        }
        if (allTracks != null) {
            allTracks.removeIf(t -> t.getId().equals(trackId));
        }
        EventBus.getInstance().publish(new TrackDeletedEvent(trackId));
    }

    public void clearCache() {
        trackCache.clear();
        userTracks = null;
        allTracks = null;
        recommendedTracks = null;
        newReleases = null;
        topTracks = null;
        lastUserTracksFetch = 0;
        lastAllTracksFetch = 0;
    }

    private List<TrackDTO> filterAndSort(List<TrackDTO> tracks, String genre, String sort) {
        List<TrackDTO> filtered = new ArrayList<>(tracks);

        if (genre != null && !genre.isEmpty() && !genre.equalsIgnoreCase("all")) {
            filtered.removeIf(t -> !genre.equalsIgnoreCase(t.getGenre()));
        }

        if (sort != null) {
            switch (sort.toLowerCase()) {
                case "popular":
                    filtered.sort((a, b) -> b.getPlays().compareTo(a.getPlays()));
                    break;
                case "newest":
                    filtered.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                    break;
                case "oldest":
                    filtered.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
                    break;
                case "liked":
                    filtered.sort((a, b) -> b.getLikes().compareTo(a.getLikes()));
                    break;
            }
        }

        return filtered;
    }

    // KONWERSJA - DOSTOSOWANA DO TWOJEGO TrackDTO
    private TrackDTO convertToTrackDTO(com.audtream.desktop.model.dto.TrackResponse response) {
        if (response == null) return null;

        TrackDTO dto = new TrackDTO();
        dto.setId(response.getId());
        dto.setTitle(response.getTitle());
        dto.setArtist(response.getArtist());
        dto.setAlbum(response.getAlbum());
        dto.setDuration(response.getDuration());
        dto.setGenre(response.getGenre());
        dto.setPlays(response.getPlays());
        dto.setLikes(response.getLikes());
        dto.setCreatedAt(response.getCreatedAt());
        dto.setUpdatedAt(response.getUpdatedAt());

        // Jeśli TrackResponse ma te pola - ustaw je
        // Jeśli nie - możesz zostawić null lub dodać mapowanie w TrackResponse
        // dto.setFileUrl(response.getFileUrl());
        // dto.setFileSize(response.getFileSize());
        // dto.setMimeType(response.getMimeType());
        // dto.setBitrate(response.getBitrate());
        // dto.setYear(response.getYear());
        // dto.setCoverUrl(response.getCoverUrl());
        // dto.setUserId(response.getUserId());
        // dto.setUsername(response.getUsername());

        return dto;
    }

    private List<TrackDTO> convertToTrackDTO(List<com.audtream.desktop.model.dto.TrackResponse> responses) {
        if (responses == null) return new ArrayList<>();
        return responses.stream()
                .map(this::convertToTrackDTO)
                .collect(Collectors.toList());
    }
}