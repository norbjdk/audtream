package com.audtream.desktop.components;

import com.audtream.desktop.manager.AppStateManager;
import com.audtream.desktop.manager.PlayerStateManager;
import com.audtream.desktop.model.dto.TrackDTO;
import com.audtream.desktop.model.event.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.util.Objects;

import static com.audtream.desktop.util.IconTool.setIcon;

public class PlayerPanel extends VBox {
    private final Button shuffleBtn = new Button();
    private final Button previousBtn = new Button();
    private final Button playBtn = new Button();
    private final Button nextBtn = new Button();
    private final Button loopBtn = new Button();

    private final Label startTimeLabel = new Label("0:00");
    private final Label endTimeLabel = new Label("0:00");
    private final Slider progressBar = new Slider();

    private final AppStateManager app = AppStateManager.getInstance();
    private boolean isUpdatingSlider = false;

    public PlayerPanel() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/player.css")).toExternalForm());
        getStyleClass().add("player-panel");
        VBox.setVgrow(this, Priority.NEVER);

        setupComponents();
        styleComponents();
        layoutComponents();
        setupEventListeners();
        setupButtonActions();
    }

    private void setupComponents() {
        setIcon(shuffleBtn, FontAwesomeSolid.RANDOM, 20);
        setIcon(previousBtn, FontAwesomeSolid.STEP_BACKWARD, 20);
        setIcon(playBtn, FontAwesomeSolid.PLAY_CIRCLE, 28);
        setIcon(nextBtn, FontAwesomeSolid.STEP_FORWARD, 20);
        setIcon(loopBtn, FontAwesomeSolid.RETWEET, 20);

        progressBar.setMin(0);
        progressBar.setMax(100);
        progressBar.setValue(0);
    }

    private void styleComponents() {
        shuffleBtn.getStyleClass().add("player-btn");
        previousBtn.getStyleClass().add("player-btn");
        playBtn.getStyleClass().add("player-btn");
        nextBtn.getStyleClass().add("player-btn");
        loopBtn.getStyleClass().add("player-btn");
    }

    private void layoutComponents() {
        HBox firstRow = new HBox();
        firstRow.setAlignment(Pos.CENTER);
        firstRow.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(firstRow, Priority.ALWAYS);

        firstRow.getStyleClass().add("player-panel-first-row");
        firstRow.getChildren().addAll(shuffleBtn, previousBtn, playBtn, nextBtn, loopBtn);

        HBox secondRow = new HBox();
        secondRow.setAlignment(Pos.CENTER);
        secondRow.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(secondRow, Priority.ALWAYS);

        HBox.setHgrow(progressBar, Priority.ALWAYS);
        progressBar.setMaxWidth(Double.MAX_VALUE);

        secondRow.getStyleClass().add("player-panel-second-row");
        secondRow.getChildren().addAll(startTimeLabel, progressBar, endTimeLabel);

        getChildren().addAll(firstRow, secondRow);
    }

    private void setupEventListeners() {
        EventBus.getInstance().subscribe(TrackChangedEvent.class, event -> {
            Platform.runLater(() -> {
                TrackDTO track = event.getNewTrack();
                if (track != null) {
                    int duration = track.getDuration() != null ? track.getDuration() : 0;
                    progressBar.setMax(duration);
                    progressBar.setValue(0);
                    endTimeLabel.setText(formatTime(duration));
                    startTimeLabel.setText("0:00");
                }
            });
        });

        EventBus.getInstance().subscribe(PlaybackStateChangedEvent.class, event -> {
            Platform.runLater(() -> {
                if (event.isPlaying()) {
                    setIcon(playBtn, FontAwesomeSolid.PAUSE_CIRCLE, 28);
                } else {
                    setIcon(playBtn, FontAwesomeSolid.PLAY_CIRCLE, 28);
                }
            });
        });

        EventBus.getInstance().subscribe(PlaybackTimeChangedEvent.class, event -> {
            Platform.runLater(() -> {
                if (!isUpdatingSlider) {
                    double currentTime = event.getCurrentTime();
                    progressBar.setValue(currentTime);
                    startTimeLabel.setText(formatTime((int) currentTime));
                }
            });
        });

        EventBus.getInstance().subscribe(ShuffleChangedEvent.class, event -> {
            Platform.runLater(() -> {
                if (event.isShuffle()) {
                    shuffleBtn.setStyle("-fx-text-fill: #1db954;");
                } else {
                    shuffleBtn.setStyle("");
                }
            });
        });

        EventBus.getInstance().subscribe(LoopModeChangedEvent.class, event -> {
            Platform.runLater(() -> {
                switch (event.getLoopMode()) {
                    case OFF:
                        loopBtn.setStyle("");
                        setIcon(loopBtn, FontAwesomeSolid.RETWEET, 20);
                        break;
                    case ALL:
                        loopBtn.setStyle("-fx-text-fill: #1db954;");
                        setIcon(loopBtn, FontAwesomeSolid.RETWEET, 20);
                        break;
                    case ONE:
                        loopBtn.setStyle("-fx-text-fill: #1db954;");
                        setIcon(loopBtn, FontAwesomeSolid.REDO, 20);
                        break;
                }
            });
        });

        progressBar.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            isUpdatingSlider = isChanging;
            if (!isChanging) {
                app.getPlayerManager().seek(progressBar.getValue());
            }
        });
    }

    private void setupButtonActions() {
        playBtn.setOnAction(e -> app.getPlayerManager().togglePlayPause());
        previousBtn.setOnAction(e -> app.getPlayerManager().playPrevious());
        nextBtn.setOnAction(e -> app.getPlayerManager().playNext());
        shuffleBtn.setOnAction(e -> app.getPlayerManager().toggleShuffle());
        loopBtn.setOnAction(e -> app.getPlayerManager().cycleLoopMode());
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }
}