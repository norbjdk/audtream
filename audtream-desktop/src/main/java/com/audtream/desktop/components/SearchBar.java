package com.audtream.desktop.components;

import com.audtream.desktop.manager.AppStateManager;
import com.audtream.desktop.model.dto.PlaylistDTO;
import javafx.scene.control.TextField;

import java.util.List;

public class SearchBar extends TextField {
    private final AppStateManager app = AppStateManager.getInstance();

    public SearchBar() {
        getStyleClass().add("navbar-search-bar");
        setPromptText("What do you want to listen? ");

        setOnAction(e -> handleSearch());
    }

    private void handleSearch() {
        String query = getText();
        if (query == null || query.trim().isEmpty()) {
            return;
        }

        new Thread(() -> {
            try {
                List<PlaylistDTO> results = app.searchPlaylists(query);
            } catch (Exception e) {
            }
        }).start();
    }
}