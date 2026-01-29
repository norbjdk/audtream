package com.audtream.desktop.controller;

import com.audtream.desktop.Audtream;
import com.audtream.desktop.components.FeedPanel;
import com.audtream.desktop.components.NavigationBar;
import com.audtream.desktop.components.PlayerBox;
import com.audtream.desktop.components.PlaylistsBox;
import com.audtream.desktop.model.event.CloseAppEvent;
import com.audtream.desktop.model.event.EventBus;
import com.audtream.desktop.service.CurrentUserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.layout.*;


import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private BorderPane root;

    private final NavigationBar navigationBar = new NavigationBar();
    private final PlaylistsBox playlistsBox = new PlaylistsBox();
    private final PlayerBox playerBox = new PlayerBox();
    private final FeedPanel feedPanel = new FeedPanel();

    private final HBox navContainer = new HBox();
    private final HBox playerContainer = new HBox();
    private final StackPane contentContainer = new StackPane();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupLayout();
        applyListeners();
    }

    private void setupLayout() {
        HBox.setHgrow(navigationBar, Priority.ALWAYS);
        navContainer.getChildren().add(navigationBar);
        root.setTop(navContainer);

        VBox playlistsContainer = new VBox();
        playlistsContainer.getChildren().add(playlistsBox);
        playlistsContainer.setPadding(new Insets(25, 3, 25, 3));
        VBox.setVgrow(playlistsBox, Priority.ALWAYS);
        root.setLeft(playlistsContainer);

        HBox.setHgrow(playerBox, Priority.ALWAYS);
        playerContainer.getChildren().add(playerBox);
        root.setBottom(playerContainer);

        contentContainer.getChildren().add(feedPanel);
        contentContainer.setPadding(new Insets(25, 3, 25, 3));
        root.setCenter(contentContainer);
    }

    public void applyListeners() {
        EventBus.getInstance().subscribe(CloseAppEvent.class, event -> close());
    }

    private void close() {
        Audtream.close();
    }

    private void minimize() {
        Audtream.minimize();
    }

    private void logout() {
        CurrentUserService.logout();
    }
}