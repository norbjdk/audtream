package com.audtream.desktop.manager;

import com.audtream.desktop.model.dto.*;
import com.audtream.desktop.model.entity.User;
import com.audtream.desktop.model.event.*;
import com.audtream.desktop.service.AudioPlayerService;
import com.audtream.desktop.service.CurrentUserService;
import java.io.IOException;
import java.util.List;

public class AppStateManager {
    private static AppStateManager instance;
    private final TrackDataManager trackManager;
    private final PlaylistDataManager playlistManager;
    private final UserDataManager userManager;
    private final PlayerStateManager playerManager;

    private AppStateManager() {
        this.trackManager = TrackDataManager.getInstance();
        this.playlistManager = PlaylistDataManager.getInstance();
        this.userManager = UserDataManager.getInstance();
        this.playerManager = PlayerStateManager.getInstance();
        setupEventListeners();
    }

    public static AppStateManager getInstance() {
        if (instance == null) {
            instance = new AppStateManager();
        }
        return instance;
    }

    private void setupEventListeners() {
        EventBus.getInstance().subscribe(LogoutUserEvent.class, event -> handleLogout());
        EventBus.getInstance().subscribe(TrackPlayedEvent.class, event -> handleTrackPlayed(event));
    }

    private void handleLogout() {
        trackManager.clearCache();
        playlistManager.clearCache();
        userManager.clearCache();
    }

    private void handleTrackPlayed(TrackPlayedEvent event) {
        // handled by managers
    }

    public List<TrackDTO> loadUserTracks(boolean forceRefresh) throws IOException {
        return trackManager.getUserTracks(forceRefresh);
    }

    public List<TrackDTO> loadAllTracks(String genre, String sort, boolean forceRefresh) throws IOException {
        return trackManager.getAllTracks(genre, sort, forceRefresh);
    }

    public List<TrackDTO> loadRecommendedTracks(boolean forceRefresh) throws IOException {
        return trackManager.getRecommendedTracks(20, forceRefresh);
    }

    public List<TrackDTO> loadNewReleases(boolean forceRefresh) throws IOException {
        return trackManager.getNewReleases(20, forceRefresh);
    }

    public List<TrackDTO> loadTopTracks(boolean forceRefresh) throws IOException {
        return trackManager.getTopTracks(50, forceRefresh);
    }

    public TrackDTO loadTrack(Long trackId) throws IOException {
        return trackManager.getTrackById(trackId, true);
    }

    public void playTrack(TrackDTO track) throws IOException {
        playerManager.playTrack(track);
        String streamUrl = trackManager.getStreamUrl(track.getId());
        EventBus.getInstance().publish(new StreamUrlLoadedEvent(streamUrl));
        AudioPlayerService.getInstance().loadAndPlay(streamUrl);
        trackManager.incrementPlayCount(track.getId());
    }

    public void playTrackFromQueue(int index) {
        playerManager.playTrackFromQueue(index);
    }

    public void toggleTrackLike(Long trackId) throws IOException {
        trackManager.toggleLike(trackId);
    }

    public void deleteTrack(Long trackId) throws IOException {
        trackManager.deleteTrack(trackId);
    }

    public List<PlaylistDTO> loadUserPlaylists(boolean forceRefresh) throws IOException {
        return playlistManager.getUserPlaylists(forceRefresh);
    }

    public List<PlaylistDTO> loadUserPublicPlaylists(Long userId) throws IOException {
        return playlistManager.getUserPublicPlaylists(userId, false);
    }

    public List<PlaylistDTO> loadTrendingPlaylists(boolean forceRefresh) throws IOException {
        return playlistManager.getTrendingPlaylists(20, forceRefresh);
    }

    public List<PlaylistDTO> loadNewPlaylists(boolean forceRefresh) throws IOException {
        return playlistManager.getNewPlaylists(20, forceRefresh);
    }

    public List<PlaylistDTO> loadTopPlaylists(boolean forceRefresh) throws IOException {
        return playlistManager.getTopPlaylists(20, forceRefresh);
    }

    public List<PlaylistDTO> searchPlaylists(String query) throws IOException {
        return playlistManager.searchPlaylists(query, 20);
    }

    public PlaylistDTO loadPlaylist(Long playlistId) throws IOException {
        return playlistManager.getPlaylist(playlistId, true);
    }

    public PlaylistDTO createPlaylist(String name, String description, Boolean isPublic) throws IOException {
        PlaylistRequest request = new PlaylistRequest();
        request.setName(name);
        request.setDescription(description);
        request.setIsPublic(isPublic);
        return playlistManager.createPlaylist(request);
    }

    public PlaylistDTO updatePlaylist(Long playlistId, String name, String description, Boolean isPublic) throws IOException {
        PlaylistRequest request = new PlaylistRequest();
        request.setName(name);
        request.setDescription(description);
        request.setIsPublic(isPublic);
        return playlistManager.updatePlaylist(playlistId, request);
    }

    public void deletePlaylist(Long playlistId) throws IOException {
        playlistManager.deletePlaylist(playlistId);
    }

    public PlaylistDTO addTrackToPlaylist(Long playlistId, Long trackId) throws IOException {
        return playlistManager.addTrack(playlistId, trackId);
    }

    public PlaylistDTO removeTrackFromPlaylist(Long playlistId, Long trackId) throws IOException {
        return playlistManager.removeTrack(playlistId, trackId);
    }

    public void playPlaylist(PlaylistDTO playlist, int startIndex) throws IOException {
        playerManager.setQueueFromPlaylist(playlist, startIndex);
        playlistManager.incrementPlayCount(playlist.getId());
    }

    public void togglePlaylistLike(Long playlistId) throws IOException {
        playlistManager.likePlaylist(playlistId);
    }

    public UserStatsDTO loadUserStats(boolean forceRefresh) throws IOException {
        return userManager.getArtistStats(forceRefresh);
    }

    public User loadCurrentUser() {
        return CurrentUserService.getCurrentUser();
    }

    public PlayerStateManager getPlayerManager() {
        return playerManager;
    }

    public TrackDataManager getTrackManager() {
        return trackManager;
    }

    public PlaylistDataManager getPlaylistManager() {
        return playlistManager;
    }

    public UserDataManager getUserManager() {
        return userManager;
    }
}