package com.audtream.desktop.controller;

import com.audtream.desktop.Audtream;
import com.audtream.desktop.service.CurrentUserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML private ImageView imgPrev;
    @FXML private GridPane topBarPane;
    @FXML private Button closeBtn;
    @FXML private Label usernameLabel;
    @FXML private VBox contentContainer;
    @FXML private VBox playlistsContainer;

    private MusicPlayerController musicPlayerController;
    private DiscoverController exploreController;
    private PlaylistsController playlistsController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupProfileImage();

        setupUserInfo();

        loadExploreView();
        loadPlaylistsComponent();
        //loadMusicPlayer();

        Audtream.applyAppMovement(topBarPane);
    }

    private void setupProfileImage() {
        imgPrev.setPreserveRatio(false);
        imgPrev.setFitHeight(150);
        imgPrev.setFitWidth(150);

        try {
            imgPrev.setImage(new Image(
                    Objects.requireNonNull(
                            getClass().getResource("/com/audtream/desktop/assets/img/temp/me.jpg")
                    ).toExternalForm()
            ));
            Circle clip = new Circle(75, 75, 75);
            imgPrev.setClip(clip);
        } catch (Exception e) {
            System.err.println("Failed to load profile image: " + e.getMessage());
        }
    }

    private void setupUserInfo() {
        String username = CurrentUserService.getUsername();
        if (username != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        } else {
            usernameLabel.setText("Welcome!");
        }
    }

    private void loadMusicPlayer() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/audtream/desktop/fxml/components/music-player.fxml")
            );
            VBox musicPlayer = loader.load();
            musicPlayerController = loader.getController();

            // Dodaj music player do kontenera
            //musicPlayerContainer.getChildren().add(musicPlayer);

        } catch (IOException e) {
            System.err.println("Failed to load music player: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPlaylistsComponent() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/audtream/desktop/fxml/components/playlists.fxml")
            );

            BorderPane playlistsComponent = loader.load();
            playlistsController = loader.getController();

            playlistsContainer.getChildren().add(playlistsComponent);
        } catch (IOException e) {
            System.err.println("Failed to load playlists component: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadExploreView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/audtream/desktop/fxml/components/explore.fxml")
            );
            StackPane exploreView = loader.load();
            exploreController = loader.getController();

            contentContainer.getChildren().add(exploreView);
            VBox.setVgrow(exploreView, Priority.ALWAYS);

        } catch (IOException e) {
            System.err.println("Failed to load explore view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void close() {
        // Cleanup music player przed zamknięciem
        if (musicPlayerController != null) {
            musicPlayerController.cleanup();
        }
        Audtream.close();
    }

    @FXML
    private void minimize() {
        Audtream.minimize();
    }

    @FXML
    private void logout() {
        // Cleanup
        if (musicPlayerController != null) {
            musicPlayerController.cleanup();
        }

        CurrentUserService.logout();

        // TODO: Powrót do ekranu logowania
    }
}