package com.audtream.desktop.components;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class TrackCard extends VBox {
    private final Long id;

    public TrackCard(Long id, String title, String author, String coverUrl) {
        this.id = id;

        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/trackcard.css")).toExternalForm());
        getStyleClass().add("track-card");

        setLayout(title, author, coverUrl);
    }

    private void setLayout(String title, String author, String coverUrl) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("track-name-label");
        Label authorLabel = new Label(author);
        authorLabel.getStyleClass().add("track-author-label");

        Image trackCover = new Image(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/song-sample/" + coverUrl)).toExternalForm());
        ImageView imageView = new ImageView(trackCover);
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        getChildren().addAll(imageView, titleLabel, authorLabel);
    }
}
