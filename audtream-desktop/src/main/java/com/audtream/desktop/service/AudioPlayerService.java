package com.audtream.desktop.service;

import com.audtream.desktop.manager.PlayerStateManager;
import com.audtream.desktop.model.event.EventBus;
import com.audtream.desktop.model.event.PlaybackTimeChangedEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class AudioPlayerService {
    private static AudioPlayerService instance;
    private MediaPlayer mediaPlayer;
    private final PlayerStateManager playerState;

    private AudioPlayerService() {
        this.playerState = PlayerStateManager.getInstance();
    }

    public static AudioPlayerService getInstance() {
        if (instance == null) {
            instance = new AudioPlayerService();
        }
        return instance;
    }

    public void loadAndPlay(String streamUrl) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            Media media = new Media(streamUrl);
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(() -> {
                mediaPlayer.setVolume(playerState.getVolume());
                if (playerState.isPlaying()) {
                    mediaPlayer.play();
                }
            });

            mediaPlayer.setOnPlaying(() -> {
                playerState.play();
            });

            mediaPlayer.setOnPaused(() -> {
                playerState.pause();
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                playerState.playNext();
            });

            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (newTime != null) {
                    double currentSeconds = newTime.toSeconds();
                    playerState.updateCurrentTime(currentSeconds);
                    EventBus.getInstance().publish(new PlaybackTimeChangedEvent(currentSeconds));
                }
            });

            mediaPlayer.setOnError(() -> {
            });

        } catch (Exception e) {
        }
    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void seek(double seconds) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(seconds));
        }
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public void dispose() {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }
}