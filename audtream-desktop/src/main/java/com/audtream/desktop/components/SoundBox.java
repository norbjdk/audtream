package com.audtream.desktop.components;

import com.audtream.desktop.manager.AppStateManager;
import com.audtream.desktop.model.event.EventBus;
import com.audtream.desktop.model.event.VolumeChangedEvent;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.util.Objects;

import static com.audtream.desktop.util.IconTool.setIcon;

public class SoundBox extends HBox {
    private final Button speakerBtn = new Button();
    private final Slider soundBar = new Slider();
    private final AppStateManager app = AppStateManager.getInstance();
    private boolean isUpdatingSlider = false;

    public SoundBox() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/player.css")).toExternalForm());
        getStyleClass().add("sound-box");

        setupComponents();
        setupEventListeners();
    }

    private void setupComponents() {
        setIcon(speakerBtn, FontAwesomeSolid.VOLUME_UP);
        speakerBtn.getStyleClass().add("sound-btn");
        soundBar.getStyleClass().add("sound-bar");

        soundBar.setMin(0);
        soundBar.setMax(1);
        soundBar.setValue(0.5);

        getChildren().add(speakerBtn);
        getChildren().add(soundBar);

        soundBar.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isUpdatingSlider) {
                app.getPlayerManager().setVolume(newVal.doubleValue());
                updateSpeakerIcon(newVal.doubleValue());
            }
        });

        speakerBtn.setOnAction(e -> toggleMute());
    }

    private void setupEventListeners() {
        EventBus.getInstance().subscribe(VolumeChangedEvent.class, event -> {
            Platform.runLater(() -> {
                isUpdatingSlider = true;
                soundBar.setValue(event.getVolume());
                updateSpeakerIcon(event.getVolume());
                isUpdatingSlider = false;
            });
        });
    }

    private void toggleMute() {
        double currentVolume = app.getPlayerManager().getVolume();
        if (currentVolume > 0) {
            app.getPlayerManager().setVolume(0);
        } else {
            app.getPlayerManager().setVolume(0.5);
        }
    }

    private void updateSpeakerIcon(double volume) {
        if (volume == 0) {
            setIcon(speakerBtn, FontAwesomeSolid.VOLUME_MUTE);
        } else if (volume < 0.5) {
            setIcon(speakerBtn, FontAwesomeSolid.VOLUME_DOWN);
        } else {
            setIcon(speakerBtn, FontAwesomeSolid.VOLUME_UP);
        }
    }
}