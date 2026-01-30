package com.audtream.desktop.service;

import com.audtream.desktop.manager.PlayerStateManager;
import com.audtream.desktop.model.event.EventBus;
import com.audtream.desktop.model.event.PlaybackTimeChangedEvent;
import javafx.application.Platform;
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
        if (streamUrl == null || streamUrl.trim().isEmpty()) {
            System.err.println("ERROR: Stream URL is null or empty!");
            return;
        }

        streamUrl = streamUrl.trim();

        if (!streamUrl.startsWith("http://") && !streamUrl.startsWith("https://")) {
            System.err.println("ERROR: Stream URL must start with http:// or https://");
            System.err.println("Received: " + streamUrl);
            return;
        }

        String finalStreamUrl = streamUrl;
        Platform.runLater(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }

            try {
                System.out.println("=== AUDIO DEBUG ===");
                System.out.println("Stream URL: " + finalStreamUrl);

                Media media = new Media(finalStreamUrl);
                mediaPlayer = new MediaPlayer(media);

                mediaPlayer.setOnReady(() -> {
                    System.out.println("✓ MediaPlayer READY");
                    System.out.println("  Duration: " + mediaPlayer.getTotalDuration());
                    System.out.println("  Volume: " + playerState.getVolume());

                    mediaPlayer.setVolume(playerState.getVolume());
                    System.out.println("  Starting playback...");
                    mediaPlayer.play();
                });

                mediaPlayer.setOnPlaying(() -> {
                    System.out.println("✓ MediaPlayer PLAYING!");
                });

                mediaPlayer.setOnPaused(() -> {
                    System.out.println("⏸ MediaPlayer PAUSED");
                });

                mediaPlayer.setOnEndOfMedia(() -> {
                    System.out.println("⏹ MediaPlayer END OF MEDIA");
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
                    System.err.println("✗ MediaPlayer ERROR!");
                    System.err.println("  Error: " + mediaPlayer.getError());
                    if (mediaPlayer.getError() != null) {
                        System.err.println("  Message: " + mediaPlayer.getError().getMessage());
                        mediaPlayer.getError().printStackTrace();
                    }
                });

                media.setOnError(() -> {
                    System.err.println("✗ Media ERROR!");
                    System.err.println("  Error: " + media.getError());
                    if (media.getError() != null) {
                        System.err.println("  Message: " + media.getError().getMessage());
                        media.getError().printStackTrace();
                    }
                });

            } catch (Exception e) {
                System.err.println("✗ EXCEPTION creating MediaPlayer!");
                System.err.println("  Message: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void play() {
        Platform.runLater(() -> {
            if (mediaPlayer != null) {
                System.out.println("▶ Play() - Status: " + mediaPlayer.getStatus());
                mediaPlayer.play();
            } else {
                System.err.println("✗ Cannot play - mediaPlayer is NULL");
            }
        });
    }

    public void pause() {
        Platform.runLater(() -> {
            if (mediaPlayer != null) {
                System.out.println("⏸ Pause()");
                mediaPlayer.pause();
            }
        });
    }

    public void stop() {
        Platform.runLater(() -> {
            if (mediaPlayer != null) {
                System.out.println("⏹ Stop()");
                mediaPlayer.stop();
            }
        });
    }

    public void seek(double seconds) {
        Platform.runLater(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(seconds));
            }
        });
    }

    public void setVolume(double volume) {
        Platform.runLater(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(volume);
            }
        });
    }

    public void dispose() {
        Platform.runLater(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.dispose();
                mediaPlayer = null;
            }
        });
    }
}