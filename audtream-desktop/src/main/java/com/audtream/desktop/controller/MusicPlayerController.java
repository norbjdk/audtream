package com.audtream.desktop.controller;

import com.audtream.desktop.model.dto.TrackResponse;
import com.audtream.desktop.service.TrackService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MusicPlayerController implements Initializable {

    @FXML private ImageView coverImageView;
    @FXML private Label trackTitleLabel;
    @FXML private Label artistLabel;
    @FXML private Label currentTimeLabel;
    @FXML private Label totalTimeLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Slider volumeSlider;
    @FXML private Button playPauseButton;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button shuffleButton;
    @FXML private Button repeatButton;

    private MediaPlayer mediaPlayer;
    private TrackService trackService;
    private Timeline progressTimeline;

    private List<TrackResponse> playlist = new ArrayList<>();
    private List<TrackResponse> shuffledPlaylist = new ArrayList<>();
    private int currentTrackIndex = 0;

    private boolean isShuffleEnabled = false;
    private RepeatMode repeatMode = RepeatMode.OFF;

    private enum RepeatMode {
        OFF,
        ONE,
        ALL
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        trackService = new TrackService();

        setupButtons();
        setupVolumeSlider();
        setupProgressBar();

        loadUserTracks();
    }

    private void setupButtons() {
        playPauseButton.setOnAction(e -> togglePlayPause());
        previousButton.setOnAction(e -> playPrevious());
        nextButton.setOnAction(e -> playNext());
        shuffleButton.setOnAction(e -> toggleShuffle());
        repeatButton.setOnAction(e -> toggleRepeat());
    }

    private void setupVolumeSlider() {
        volumeSlider.setValue(50);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newVal.doubleValue() / 100.0);
            }
        });
    }

    private void setupProgressBar() {
        progressBar.setOnMouseClicked(event -> {
            if (mediaPlayer != null) {
                double mouseX = event.getX();
                double width = progressBar.getWidth();
                double percentage = mouseX / width;

                Duration totalDuration = mediaPlayer.getTotalDuration();
                Duration seekTime = totalDuration.multiply(percentage);
                mediaPlayer.seek(seekTime);
            }
        });
    }

    private void loadUserTracks() {
        new Thread(() -> {
            try {
                playlist = trackService.getUserTracks();

                Platform.runLater(() -> {
                    if (!playlist.isEmpty()) {
                        playTrack(0);
                    } else {
                        trackTitleLabel.setText("No tracks available");
                        artistLabel.setText("Upload some music first!");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    trackTitleLabel.setText("Error loading tracks");
                    artistLabel.setText(e.getMessage());
                });
            }
        }).start();
    }

    private void playTrack(int index) {
        List<TrackResponse> currentPlaylist = isShuffleEnabled ? shuffledPlaylist : playlist;

        if (currentPlaylist.isEmpty() || index < 0 || index >= currentPlaylist.size()) {
            return;
        }

        currentTrackIndex = index;
        TrackResponse track = currentPlaylist.get(index);

        new Thread(() -> {
            try {
                String streamUrl = trackService.getTrackStreamUrl(track.getId());

                Platform.runLater(() -> {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.dispose();
                    }

                    Media media = new Media(streamUrl);
                    mediaPlayer = new MediaPlayer(media);

                    mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);

                    mediaPlayer.setOnReady(() -> {
                        updateTrackInfo(track);
                        mediaPlayer.play();
                        playPauseButton.setText("⏸");
                        startProgressUpdater();

                        incrementPlayCount(track.getId());
                    });

                    mediaPlayer.setOnEndOfMedia(() -> {
                        handleTrackEnd();
                    });

                    mediaPlayer.setOnError(() -> {
                        System.err.println("Media error: " + mediaPlayer.getError());
                        trackTitleLabel.setText("Playback error");
                    });
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    trackTitleLabel.setText("Error playing track");
                    artistLabel.setText(e.getMessage());
                });
            }
        }).start();
    }

    private void updateTrackInfo(TrackResponse track) {
        trackTitleLabel.setText(track.getTitle());
        artistLabel.setText(track.getArtist());
        totalTimeLabel.setText(track.getFormattedDuration());
        currentTimeLabel.setText("0:00");

        if (track.getCoverUrl() != null && !track.getCoverUrl().isEmpty()) {
            try {
                Image coverImage = new Image(track.getCoverUrl(), true);
                coverImageView.setImage(coverImage);
            } catch (Exception e) {
                setDefaultCover();
            }
        } else {
            setDefaultCover();
        }
    }

    private void setDefaultCover() {
        try {
            Image defaultImage = new Image(getClass().getResource("/com/audtream/desktop/assets/img/default-cover.png").toExternalForm());
            coverImageView.setImage(defaultImage);
        } catch (Exception e) {
        }
    }

    private void startProgressUpdater() {
        if (progressTimeline != null) {
            progressTimeline.stop();
        }

        progressTimeline = new Timeline(
                new KeyFrame(Duration.millis(100), event -> {
                    if (mediaPlayer != null) {
                        Duration currentTime = mediaPlayer.getCurrentTime();
                        Duration totalTime = mediaPlayer.getTotalDuration();

                        if (totalTime.greaterThan(Duration.ZERO)) {
                            double progress = currentTime.toMillis() / totalTime.toMillis();
                            progressBar.setProgress(progress);

                            int seconds = (int) currentTime.toSeconds();
                            int minutes = seconds / 60;
                            seconds = seconds % 60;
                            currentTimeLabel.setText(String.format("%d:%02d", minutes, seconds));
                        }
                    }
                })
        );
        progressTimeline.setCycleCount(Timeline.INDEFINITE);
        progressTimeline.play();
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) {
            if (!playlist.isEmpty()) {
                playTrack(currentTrackIndex);
            }
            return;
        }

        MediaPlayer.Status status = mediaPlayer.getStatus();

        if (status == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseButton.setText("▶");
            if (progressTimeline != null) {
                progressTimeline.pause();
            }
        } else {
            mediaPlayer.play();
            playPauseButton.setText("⏸");
            if (progressTimeline != null) {
                progressTimeline.play();
            }
        }
    }

    private void playPrevious() {
        List<TrackResponse> currentPlaylist = isShuffleEnabled ? shuffledPlaylist : playlist;

        if (currentPlaylist.isEmpty()) return;

        currentTrackIndex--;
        if (currentTrackIndex < 0) {
            currentTrackIndex = currentPlaylist.size() - 1;
        }

        playTrack(currentTrackIndex);
    }

    private void playNext() {
        List<TrackResponse> currentPlaylist = isShuffleEnabled ? shuffledPlaylist : playlist;

        if (currentPlaylist.isEmpty()) return;

        currentTrackIndex++;
        if (currentTrackIndex >= currentPlaylist.size()) {
            currentTrackIndex = 0;
        }

        playTrack(currentTrackIndex);
    }

    private void handleTrackEnd() {
        switch (repeatMode) {
            case ONE:
                playTrack(currentTrackIndex);
                break;
            case ALL:
                playNext();
                break;
            case OFF:
            default:
                List<TrackResponse> currentPlaylist = isShuffleEnabled ? shuffledPlaylist : playlist;
                if (currentTrackIndex < currentPlaylist.size() - 1) {
                    playNext();
                } else {
                    mediaPlayer.stop();
                    playPauseButton.setText("▶");
                }
                break;
        }
    }

    private void toggleShuffle() {
        isShuffleEnabled = !isShuffleEnabled;

        if (isShuffleEnabled) {
            shuffledPlaylist = new ArrayList<>(playlist);
            Collections.shuffle(shuffledPlaylist);
            shuffleButton.setStyle("-fx-text-fill: #1db954;");
        } else {
            shuffleButton.setStyle("");
        }
    }

    private void toggleRepeat() {
        switch (repeatMode) {
            case OFF:
                repeatMode = RepeatMode.ALL;
                repeatButton.setText("🔁");
                repeatButton.setStyle("-fx-text-fill: #1db954;");
                break;
            case ALL:
                repeatMode = RepeatMode.ONE;
                repeatButton.setText("🔂");
                break;
            case ONE:
                repeatMode = RepeatMode.OFF;
                repeatButton.setText("🔁");
                repeatButton.setStyle("");
                break;
        }
    }

    private void incrementPlayCount(Long trackId) {
        new Thread(() -> {
            try {
                trackService.incrementPlayCount(trackId);
            } catch (Exception e) {
                System.err.println("Failed to increment play count: " + e.getMessage());
            }
        }).start();
    }

    public void cleanup() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        if (progressTimeline != null) {
            progressTimeline.stop();
        }
    }
}
