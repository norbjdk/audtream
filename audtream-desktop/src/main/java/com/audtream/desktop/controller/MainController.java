package com.audtream.desktop.controller;

import com.audtream.desktop.Audtream;
import com.audtream.desktop.components.*;
import com.audtream.desktop.model.event.CloseAppEvent;
import com.audtream.desktop.model.event.EnterProfileEvent;
import com.audtream.desktop.model.event.EventBus;
import com.audtream.desktop.model.event.LogoutUserEvent;
import com.audtream.desktop.service.CurrentUserService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    @FXML private BorderPane root;

    private final NavigationBar navigationBar = new NavigationBar();
    private final PlaylistsBox playlistsBox = new PlaylistsBox();
    private final PlayerBox playerBox = new PlayerBox();
    private final FeedPanel feedPanel = new FeedPanel();
    private final ProfilePanel profilePanel = new ProfilePanel();

    private final HBox navContainer = new HBox();
    private final HBox playerContainer = new HBox();
    private final StackPane contentContainer = new StackPane();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupLayout();
        applyListeners();
    }

    private void changeContent(String contentView) {
        if (contentView.isEmpty()) return;
        contentContainer.getChildren().clear();
        switch (contentView) {
            case "feed" -> contentContainer.getChildren().add(feedPanel);
            case "profile" -> contentContainer.getChildren().add(profilePanel);
        }
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

        changeContent("feed");
        contentContainer.setPadding(new Insets(25, 3, 25, 3));
        root.setCenter(contentContainer);
    }

    public void applyListeners() {
        EventBus.getInstance().subscribe(CloseAppEvent.class, event -> close());
        EventBus.getInstance().subscribe(LogoutUserEvent.class, event -> logout());
        EventBus.getInstance().subscribe(EnterProfileEvent.class, event -> {
            changeContent("profile");
        });
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