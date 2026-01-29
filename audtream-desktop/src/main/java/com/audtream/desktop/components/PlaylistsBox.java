package com.audtream.desktop.components;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.audtream.desktop.util.IconTool.setIcon;

public class PlaylistsBox extends VBox {
    private final List<Node> rows = new ArrayList<>();
    private final List<PlaylistCard> playlists = new ArrayList<>();

    private final Button createPlaylistBtn = new Button("Create");
    private final Button playlistsFilterBtn = new Button("Playlists");
    private final Button albumsFilterBtn = new Button("Albums");
    private final Button artistsFiltersBtn = new Button("Artists");


    public PlaylistsBox() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/playlists.css")).toExternalForm());
        getStyleClass().add("playlists-box");

        setupComponents();
        addComponents();
    }

    private void addComponents() {
        for (Node row : rows) {
            getChildren().add(row);
        }
    }

    private void setupComponents() {
        // First row
        setIcon(createPlaylistBtn, FontAwesomeSolid.PLUS);
        HBox firstRow = new HBox();
        firstRow.getStyleClass().add("first-row");
        Label libraryLabel = new Label("Library");
        firstRow.getChildren().addAll(libraryLabel, createPlaylistBtn);
        rows.add(firstRow);

        // Second row
        HBox secondRow = new HBox();
        secondRow.getStyleClass().add("second-row");
        secondRow.getChildren().addAll(playlistsFilterBtn, albumsFilterBtn, artistsFiltersBtn);
        rows.add(secondRow);

        // Third row
        VBox thirdRow = new VBox();
        thirdRow.getStyleClass().add("third-row");
        thirdRow.getChildren().add(new PlaylistCard(0L, "Liked Songs", "You", "https://education.oracle.com/file/general/p-80-java.png"));
        thirdRow.getChildren().add(new PlaylistCard(0L, "Rainy Day", "AudTream", "https://education.oracle.com/file/general/p-80-java.png"));
        thirdRow.getChildren().add(new PlaylistCard(0L, "RUSH!", "Maneskin", "https://education.oracle.com/file/general/p-80-java.png"));
        thirdRow.getChildren().add(new PlaylistCard(0L, "Broken Heart", "AudTream", "https://education.oracle.com/file/general/p-80-java.png"));
        thirdRow.getChildren().add(new PlaylistCard(0L, "Car Songs", "AudTream", "https://education.oracle.com/file/general/p-80-java.png"));
        thirdRow.getChildren().add(new PlaylistCard(0L, "Slowed Songs + Reverb", "musicenjoyer3000", "https://education.oracle.com/file/general/p-80-java.png"));
        rows.add(thirdRow);
    }
}
