package com.audtream.desktop.util;

import com.audtream.desktop.manager.AppStateManager;
import com.audtream.desktop.manager.PlayerStateManager;
import com.audtream.desktop.model.dto.*;
import com.audtream.desktop.model.event.*;
import javafx.application.Platform;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class DataBindingHelper {
    private final AppStateManager appState;

    public DataBindingHelper() {
        this.appState = AppStateManager.getInstance();
    }

    public void bindUserTracks(Consumer<List<TrackDTO>> onSuccess, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                List<TrackDTO> tracks = appState.loadUserTracks(false);
                Platform.runLater(() -> onSuccess.accept(tracks));
            } catch (IOException e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }

    public void bindAllTracks(String genre, String sort, Consumer<List<TrackDTO>> onSuccess, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                List<TrackDTO> tracks = appState.loadAllTracks(genre, sort, false);
                Platform.runLater(() -> onSuccess.accept(tracks));
            } catch (IOException e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }

    public void bindRecommendedTracks(Consumer<List<TrackDTO>> onSuccess, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                List<TrackDTO> tracks = appState.loadRecommendedTracks(false);
                Platform.runLater(() -> onSuccess.accept(tracks));
            } catch (IOException e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }

    public void bindNewReleases(Consumer<List<TrackDTO>> onSuccess, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                List<TrackDTO> tracks = appState.loadNewReleases(false);
                Platform.runLater(() -> onSuccess.accept(tracks));
            } catch (IOException e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }

    public void bindTopTracks(Consumer<List<TrackDTO>> onSuccess, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                List<TrackDTO> tracks = appState.loadTopTracks(false);
                Platform.runLater(() -> onSuccess.accept(tracks));
            } catch (IOException e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }

    public void bindUserPlaylists(Consumer<List<PlaylistDTO>> onSuccess, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                List<PlaylistDTO> playlists = appState.loadUserPlaylists(false);
                Platform.runLater(() -> onSuccess.accept(playlists));
            } catch (IOException e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }

    public void bindTrendingPlaylists(Consumer<List<PlaylistDTO>> onSuccess, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                List<PlaylistDTO> playlists = appState.loadTrendingPlaylists(false);
                Platform.runLater(() -> onSuccess.accept(playlists));
            } catch (IOException e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }

    public void bindUserStats(Consumer<UserStatsDTO> onSuccess, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                UserStatsDTO stats = appState.loadUserStats(false);
                Platform.runLater(() -> onSuccess.accept(stats));
            } catch (IOException e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }

    public void bindPlaylist(Long playlistId, Consumer<PlaylistDTO> onSuccess, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                PlaylistDTO playlist = appState.loadPlaylist(playlistId);
                Platform.runLater(() -> onSuccess.accept(playlist));
            } catch (IOException e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }

    public void subscribeToTrackChanged(Consumer<TrackChangedEvent> handler) {
        EventBus.getInstance().subscribe(TrackChangedEvent.class, event ->
                Platform.runLater(() -> handler.accept(event))
        );
    }

    public void subscribeToPlaybackStateChanged(Consumer<PlaybackStateChangedEvent> handler) {
        EventBus.getInstance().subscribe(PlaybackStateChangedEvent.class, event ->
                Platform.runLater(() -> handler.accept(event))
        );
    }

    public void subscribeToQueueChanged(Consumer<QueueChangedEvent> handler) {
        EventBus.getInstance().subscribe(QueueChangedEvent.class, event ->
                Platform.runLater(() -> handler.accept(event))
        );
    }

    public void subscribeToVolumeChanged(Consumer<VolumeChangedEvent> handler) {
        EventBus.getInstance().subscribe(VolumeChangedEvent.class, event ->
                Platform.runLater(() -> handler.accept(event))
        );
    }

    public void subscribeToPlaybackTimeChanged(Consumer<PlaybackTimeChangedEvent> handler) {
        EventBus.getInstance().subscribe(PlaybackTimeChangedEvent.class, event ->
                Platform.runLater(() -> handler.accept(event))
        );
    }

    public void subscribeToShuffleChanged(Consumer<ShuffleChangedEvent> handler) {
        EventBus.getInstance().subscribe(ShuffleChangedEvent.class, event ->
                Platform.runLater(() -> handler.accept(event))
        );
    }

    public void subscribeToLoopModeChanged(Consumer<LoopModeChangedEvent> handler) {
        EventBus.getInstance().subscribe(LoopModeChangedEvent.class, event ->
                Platform.runLater(() -> handler.accept(event))
        );
    }

    public void subscribeToDataRefreshed(Consumer<DataRefreshedEvent> handler) {
        EventBus.getInstance().subscribe(DataRefreshedEvent.class, event ->
                Platform.runLater(() -> handler.accept(event))
        );
    }

    public void subscribeToPlaylistCreated(Consumer<PlaylistCreatedEvent> handler) {
        EventBus.getInstance().subscribe(PlaylistCreatedEvent.class, event ->
                Platform.runLater(() -> handler.accept(event))
        );
    }

    public void subscribeToPlaylistUpdated(Consumer<PlaylistUpdatedEvent> handler) {
        EventBus.getInstance().subscribe(PlaylistUpdatedEvent.class, event ->
                Platform.runLater(() -> handler.accept(event))
        );
    }

    public void subscribeToPlaylistDeleted(Consumer<PlaylistDeletedEvent> handler) {
        EventBus.getInstance().subscribe(PlaylistDeletedEvent.class, event ->
                Platform.runLater(() -> handler.accept(event))
        );
    }

    public void subscribeToTrackDeleted(Consumer<TrackDeletedEvent> handler) {
        EventBus.getInstance().subscribe(TrackDeletedEvent.class, event ->
                Platform.runLater(() -> handler.accept(event))
        );
    }

    public PlayerStateManager getPlayerState() {
        return appState.getPlayerManager();
    }
}