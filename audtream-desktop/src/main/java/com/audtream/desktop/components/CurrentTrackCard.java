package com.audtream.desktop.components;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.Objects;

public class CurrentTrackCard extends GridPane {

    public CurrentTrackCard(Long id, String name, String author, String coverUrl) {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/player.css")).toExternalForm());
        getStyleClass().add("track-card");

        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(coverUrl)).toExternalForm(), true));
        imageView.setSmooth(true);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("current-track-name-label");
        Label authorLabel = new Label(author);
        authorLabel.getStyleClass().add("current-track-author-label");

        add(imageView, 0, 0, 1, 2);
        add(nameLabel, 1, 0);
        add(authorLabel, 1, 1);
    }
}
