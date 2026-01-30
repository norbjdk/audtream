package com.audtream.desktop.components;

import com.audtream.desktop.Audtream;
import com.audtream.desktop.model.dto.internal.EnterProfileRequest;
import com.audtream.desktop.model.event.*;
import com.audtream.desktop.service.CurrentUserService;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;


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
    private final Button profileBtn = new Button();
    private final Button logoutBtn = new Button();

    private final SearchBar searchBar = new SearchBar();

    public NavigationBar() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/navbar.css")).toExternalForm());
        getStyleClass().add("navbar");

        Audtream.applyAppMovement(this);

        addComponents();
        setupStyles();
        applyEvents();
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
        setIcon(profileBtn, FontAwesomeSolid.USER);
        setIcon(logoutBtn, FontAwesomeSolid.SIGN_OUT_ALT);

        add(optionsBtn, 0, 0);
        add(backwardBtn, 1, 0);
        add(forwardBtn, 2, 0);
        add(homeBtn, 3, 0);
        add(lSpacer, 4, 0);
        add(exploreBtn, 5, 0);
        add(searchBar, 6, 0);
        add(profileBtn, 7, 0);
        add(rSpacer, 8, 0);
        add(logoutBtn, 9, 0);
        add(notificationsBtn, 10, 0);
        add(minimizeBtn, 11, 0);
        add(resizeBtn, 12, 0);
        add(closeBtn, 13, 0);

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
        profileBtn.getStyleClass().add("navbar-btn");
        logoutBtn.getStyleClass().add("navbar-btn");
    }

    private void applyEvents() {
        closeBtn.setOnAction(actionEvent -> EventBus.getInstance().publish(new CloseAppEvent()));
        logoutBtn.setOnAction(actionEvent -> EventBus.getInstance().publish(new LogoutUserEvent()));
        profileBtn.setOnAction(actionEvent -> {
            EnterProfileRequest request = new EnterProfileRequest();
            request.setId(CurrentUserService.getUserId());
            EventBus.getInstance().publish(new EnterProfileEvent(request));
        });
        homeBtn.setOnAction(actionEvent -> {
            EventBus.getInstance().publish(new EnterFeedEvent());
        });
    }
}
