package com.audtream.desktop.controller;

import com.audtream.desktop.model.dto.PlaylistRequest;
import com.audtream.desktop.model.dto.PlaylistResponse;
import com.audtream.desktop.service.PlaylistService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.IOException;
import java.util.List;

public class PlaylistsController {

    @FXML private FlowPane playlistsGrid;

    private PlaylistService playlistService = new PlaylistService();
    private ObservableList<PlaylistResponse> playlists = FXCollections.observableArrayList();
    private VBox selectedPlaylistCard = null;

    @FXML
    public void initialize() {
        loadUserPlaylists();
    }

    private void loadUserPlaylists() {
        try {
            List<PlaylistResponse> userPlaylists = playlistService.getUserPlaylists();
            playlists.setAll(userPlaylists);
            renderPlaylistCards();
        } catch (IOException e) {
            showError("Błąd ładowania playlist", e.getMessage());
        }
    }

    private void renderPlaylistCards() {
        playlistsGrid.getChildren().clear();

        for (PlaylistResponse playlist : playlists) {
            VBox card = createPlaylistCard(playlist);
            playlistsGrid.getChildren().add(card);
        }
    }

    private VBox createPlaylistCard(PlaylistResponse playlist) {
        VBox card = new VBox();
        card.getStyleClass().add("playlist-card");
        card.setAlignment(Pos.CENTER);
        card.setSpacing(10);
        card.setPrefSize(180, 180);

        // Cover image
        StackPane coverContainer = new StackPane();
        coverContainer.getStyleClass().add("playlist-cover");

        if (playlist.getCoverImageUrl() != null && !playlist.getCoverImageUrl().isEmpty()) {
            try {
                ImageView imageView = new ImageView(new Image(playlist.getCoverImageUrl(), true));
                imageView.setFitWidth(140);
                imageView.setFitHeight(140);
                imageView.setPreserveRatio(true);
                coverContainer.getChildren().add(imageView);
            } catch (Exception e) {
                // Use default icon if image fails to load
                Label iconLabel = new Label("♪");
                iconLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: #b3b3b3;");
                coverContainer.getChildren().add(iconLabel);
            }
        } else {
            // Default icon for playlists without cover
            Label iconLabel = new Label("♪");
            iconLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: #b3b3b3;");
            coverContainer.getChildren().add(iconLabel);
        }

        // Playlist name
        Label nameLabel = new Label(playlist.getName());
        nameLabel.getStyleClass().add("playlist-name");
        nameLabel.setMaxWidth(160);
        nameLabel.setWrapText(true);

        // Track count
        Label tracksLabel = new Label(playlist.getTrackCountText());
        tracksLabel.getStyleClass().add("playlist-tracks");

        card.getChildren().addAll(coverContainer, nameLabel, tracksLabel);

        // Click handler
        card.setOnMouseClicked(event -> {
            // Remove selection from previously selected card
            if (selectedPlaylistCard != null) {
                selectedPlaylistCard.getStyleClass().remove("playlist-card-selected");
            }

            // Add selection to current card
            card.getStyleClass().add("playlist-card-selected");
            selectedPlaylistCard = card;

            // Show playlist details dialog
            showPlaylistDetails(playlist);
        });

        return card;
    }

    private void showPlaylistDetails(PlaylistResponse playlist) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(playlist.getName());
        dialog.setHeaderText("Szczegóły playlisty");

        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(20));
        content.setPrefSize(400, 500);

        // Cover image
        if (playlist.getCoverImageUrl() != null && !playlist.getCoverImageUrl().isEmpty()) {
            ImageView cover = new ImageView(new Image(playlist.getCoverImageUrl()));
            cover.setFitWidth(200);
            cover.setFitHeight(200);
            cover.setPreserveRatio(true);
            content.getChildren().add(cover);
        }

        // Info
        VBox infoBox = new VBox(5);

        Label nameLabel = new Label(playlist.getName());
        nameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label descriptionLabel = new Label(playlist.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-text-fill: #b3b3b3;");

        // Stats
        HBox statsBox = new HBox(20);

        VBox tracksBox = new VBox(2);
        Label tracksTitle = new Label("UTWORÓW");
        tracksTitle.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");
        Label tracksValue = new Label(playlist.getTrackCountText());
        tracksValue.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        tracksBox.getChildren().addAll(tracksTitle, tracksValue);

        VBox durationBox = new VBox(2);
        Label durationTitle = new Label("CZAS");
        durationTitle.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");
        Label durationValue = new Label(playlist.getFormattedDuration());
        durationValue.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        durationBox.getChildren().addAll(durationTitle, durationValue);

        statsBox.getChildren().addAll(tracksBox, durationBox);

        infoBox.getChildren().addAll(nameLabel, descriptionLabel, statsBox);
        content.getChildren().add(infoBox);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button playButton = new Button("Odtwórz");
        playButton.setStyle("-fx-background-color: #1DB954; -fx-text-fill: white; -fx-font-weight: bold;");
        playButton.setOnAction(e -> {
            // TODO: Implement playlist playback
            dialog.close();
        });

        Button deleteButton = new Button("Usuń");
        deleteButton.setStyle("-fx-background-color: #e22134; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            handleDeletePlaylist(playlist);
            dialog.close();
        });

        buttonBox.getChildren().addAll(playButton, deleteButton);
        content.getChildren().add(buttonBox);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.show();
    }

    @FXML
    private void handleCreatePlaylist() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nowa playlista");
        dialog.setHeaderText("Podaj nazwę nowej playlisty");
        dialog.setContentText("Nazwa:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                PlaylistRequest request = new PlaylistRequest();
                request.setName(name);
                request.setDescription("");
                request.setIsPublic(true);

                try {
                    playlistService.createPlaylist(request);
                    loadUserPlaylists(); // Refresh the grid
                    showSuccess("Playlista utworzona", "Playlista \"" + name + "\" została utworzona.");
                } catch (IOException e) {
                    showError("Błąd tworzenia playlisty", e.getMessage());
                }
            }
        });
    }

    private void handleDeletePlaylist(PlaylistResponse playlist) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuń playlistę");
        alert.setHeaderText("Czy na pewno chcesz usunąć playlistę?");
        alert.setContentText("Playlista: " + playlist.getName());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    playlistService.deletePlaylist(playlist.getId());
                    playlists.remove(playlist);
                    renderPlaylistCards(); // Refresh the grid
                    showSuccess("Playlista usunięta", "Playlista została usunięta.");
                } catch (IOException e) {
                    showError("Błąd usuwania playlisty", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleExploreTrending() {
        try {
            List<PlaylistResponse> trending = playlistService.getTrendingPlaylists(20);
            showPlaylistsDialog("Trendujące playlisty", trending);
        } catch (IOException e) {
            showError("Błąd ładowania playlist", e.getMessage());
        }
    }

    private void showPlaylistsDialog(String title, List<PlaylistResponse> playlistsList) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);

        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(20));

        for (PlaylistResponse playlist : playlistsList) {
            HBox playlistRow = new HBox(10);
            playlistRow.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(playlist.getName());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

            Label tracksLabel = new Label("• " + playlist.getTrackCountText());
            tracksLabel.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");

            playlistRow.getChildren().addAll(nameLabel, tracksLabel);
            content.getChildren().add(playlistRow);
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.show();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}