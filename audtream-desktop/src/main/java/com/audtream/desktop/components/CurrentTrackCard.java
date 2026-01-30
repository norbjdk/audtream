package com.audtream.desktop.components;

import com.audtream.desktop.model.dto.TrackDTO;
import com.audtream.desktop.model.event.EventBus;
import com.audtream.desktop.model.event.TrackChangedEvent;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.Objects;

public class CurrentTrackCard extends GridPane {
    private final ImageView imageView;
    private final Label nameLabel;
    private final Label authorLabel;

    public CurrentTrackCard(Long id, String name, String author, String coverUrl) {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/player.css")).toExternalForm());
        getStyleClass().add("track-card");

        imageView = new ImageView();
        if (coverUrl != null && !coverUrl.isEmpty()) {
            try {
                imageView.setImage(new Image(Objects.requireNonNull(getClass().getResource(coverUrl)).toExternalForm(), true));
            } catch (Exception e) {
                imageView.setImage(null);
            }
        }
        imageView.setSmooth(true);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        nameLabel = new Label(name);
        nameLabel.getStyleClass().add("current-track-name-label");
        authorLabel = new Label(author);
        authorLabel.getStyleClass().add("current-track-author-label");

        add(imageView, 0, 0, 1, 2);
        add(nameLabel, 1, 0);
        add(authorLabel, 1, 1);

        setupEventListeners();
    }

    private void setupEventListeners() {
        EventBus.getInstance().subscribe(TrackChangedEvent.class, event -> {
            Platform.runLater(() -> updateTrack(event.getNewTrack()));
        });
    }

    private void updateTrack(TrackDTO track) {
        if (track == null) {
            nameLabel.setText("No song selected");
            authorLabel.setText("No author");
            imageView.setImage(null);
            return;
        }

        nameLabel.setText(track.getTitle());
        authorLabel.setText(track.getArtist());

        if (track.getCoverUrl() != null && !track.getCoverUrl().isEmpty()) {
            try {
                imageView.setImage(new Image(track.getCoverUrl(), true));
            } catch (Exception e) {
                imageView.setImage(null);
            }
        } else {
            imageView.setImage(null);
        }
    }
}