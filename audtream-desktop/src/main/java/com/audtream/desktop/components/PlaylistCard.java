package com.audtream.desktop.components;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.Objects;

public class PlaylistCard extends GridPane {
    public PlaylistCard(Long id, String name, String author, String coverUrl) {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/playlists.css")).toExternalForm());
        getStyleClass().add("playlist-card");

        ImageView imageView = new ImageView(new Image(coverUrl, true));

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("playlist-name-label");
        Label authorLabel = new Label("Author: " + author);
        authorLabel.getStyleClass().add("playlist-author-label");

        add(imageView, 0, 0, 1, 2);
        add(nameLabel, 1, 0);
        add(authorLabel, 1, 1);
    }
}
