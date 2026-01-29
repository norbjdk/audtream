package com.audtream.desktop.components;

import javafx.scene.control.TextField;

public class SearchBar extends TextField {
    public SearchBar() {
        getStyleClass().add("navbar-search-bar");
        setPromptText("What do you want to listen? ");
    }
}
