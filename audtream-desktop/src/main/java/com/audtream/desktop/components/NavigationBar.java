package com.audtream.desktop.components;

import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Objects;

import static com.audtream.desktop.util.IconTool.setIcon;

public class NavigationBar extends GridPane {
    private final Button optionsBtn = new Button();
    private final Button minimizeBtn = new Button();
    private final Button resizeBtn = new Button();
    private final Button closeBtn = new Button();
    private final Button backwardBtn = new Button();
    private final Button forwardBtn = new Button();
    private final Button homeBtn = new Button();
    private final Button notificationsBtn = new Button();
    private final Button exploreBtn = new Button();

    private final SearchBar searchBar = new SearchBar();

    public NavigationBar() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/navbar.css")).toExternalForm());
        getStyleClass().add("navbar");

        addComponents();
        setupStyles();
    }

    private void addComponents() {
        Region lSpacer = new Region();
        Region rSpacer = new Region();
        GridPane.setHgrow(lSpacer, Priority.ALWAYS);
        GridPane.setHgrow(rSpacer, Priority.ALWAYS);

        setIcon(minimizeBtn, FontAwesomeSolid.WINDOW_MINIMIZE);
        setIcon(resizeBtn, FontAwesomeSolid.WINDOW_MAXIMIZE);
        setIcon(closeBtn, FontAwesomeSolid.WINDOW_CLOSE);
        setIcon(backwardBtn, FontAwesomeSolid.CHEVRON_LEFT);
        setIcon(forwardBtn, FontAwesomeSolid.CHEVRON_RIGHT);
        setIcon(homeBtn, FontAwesomeSolid.HOME);
        setIcon(notificationsBtn, FontAwesomeSolid.BELL);
        setIcon(exploreBtn, FontAwesomeSolid.COMPASS);
        setIcon(optionsBtn, FontAwesomeSolid.ELLIPSIS_V);

        add(optionsBtn, 0, 0);
        add(backwardBtn, 1, 0);
        add(forwardBtn, 2, 0);
        add(homeBtn, 3, 0);
        add(lSpacer, 4, 0);
        add(exploreBtn, 5, 0);
        add(searchBar, 6, 0);
        add(rSpacer, 7, 0);
        add(notificationsBtn, 8, 0);
        add(minimizeBtn, 9, 0);
        add(resizeBtn, 10, 0);
        add(closeBtn, 11, 0);

        getChildren().forEach(child -> {
            GridPane.setValignment(child, VPos.CENTER);
        });
    }

    private void setupStyles()  {
        minimizeBtn.getStyleClass().add("navbar-window-btn");
        resizeBtn.getStyleClass().add("navbar-window-btn");
        closeBtn.getStyleClass().add("navbar-window-btn");

        forwardBtn.getStyleClass().add("navbar-page-btn");
        backwardBtn.getStyleClass().add("navbar-page-btn");
        optionsBtn.getStyleClass().add("navbar-page-btn");

        homeBtn.getStyleClass().add("navbar-btn");
        notificationsBtn.getStyleClass().add("navbar-btn");
        exploreBtn.getStyleClass().add("navbar-btn");
    }
}
