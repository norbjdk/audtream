package com.audtream.desktop.components;

import com.audtream.desktop.manager.AppStateManager;
import com.audtream.desktop.model.dto.PlaylistDTO;
import com.audtream.desktop.util.DataBindingHelper;
import javafx.application.Platform;
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
    private final VBox thirdRow = new VBox();

    private final Button createPlaylistBtn = new Button("Create");
    private final Button playlistsFilterBtn = new Button("Playlists");
    private final Button albumsFilterBtn = new Button("Albums");
    private final Button artistsFiltersBtn = new Button("Artists");

    private final DataBindingHelper binder = new DataBindingHelper();
    private final AppStateManager app = AppStateManager.getInstance();

    public PlaylistsBox() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/playlists.css")).toExternalForm());
        getStyleClass().add("playlists-box");

        setupComponents();
        addComponents();
        loadData();
        setupEventListeners();
    }

    private void addComponents() {
        for (Node row : rows) {
            getChildren().add(row);
        }
    }

    private void setupComponents() {
        setIcon(createPlaylistBtn, FontAwesomeSolid.PLUS);
        HBox firstRow = new HBox();
        firstRow.getStyleClass().add("first-row");
        Label libraryLabel = new Label("Library");
        firstRow.getChildren().addAll(libraryLabel, createPlaylistBtn);
        rows.add(firstRow);

        HBox secondRow = new HBox();
        secondRow.getStyleClass().add("second-row");
        secondRow.getChildren().addAll(playlistsFilterBtn, albumsFilterBtn, artistsFiltersBtn);
        rows.add(secondRow);

        thirdRow.getStyleClass().add("third-row");
        rows.add(thirdRow);

        createPlaylistBtn.setOnAction(e -> handleCreatePlaylist());
    }

    private void loadData() {
        binder.bindUserPlaylists(
                playlists -> {
                    Platform.runLater(() -> {
                        thirdRow.getChildren().clear();

                        for (PlaylistDTO playlist : playlists) {
                            PlaylistCard card = new PlaylistCard(
                                    playlist.getId(),
                                    playlist.getName(),
                                    playlist.getUsername(),
                                    playlist.getCoverImageUrl()
                            );

                            card.setOnMouseClicked(event -> handlePlaylistClick(playlist));
                            thirdRow.getChildren().add(card);
                        }
                    });
                },
                error -> {
                }
        );
    }

    private void setupEventListeners() {
        binder.subscribeToPlaylistCreated(event -> {
            loadData();
        });

        binder.subscribeToPlaylistDeleted(event -> {
            loadData();
        });

        binder.subscribeToPlaylistUpdated(event -> {
            loadData();
        });
    }

    private void handleCreatePlaylist() {
        new Thread(() -> {
            try {
                app.createPlaylist("New Playlist", "", true);
            } catch (Exception e) {
            }
        }).start();
    }

    private void handlePlaylistClick(PlaylistDTO playlist) {
        new Thread(() -> {
            try {
                app.playPlaylist(playlist, 0);
            } catch (Exception e) {
            }
        }).start();
    }
}