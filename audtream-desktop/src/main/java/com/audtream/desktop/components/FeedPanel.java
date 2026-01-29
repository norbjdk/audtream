package com.audtream.desktop.components;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.util.Objects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class FeedPanel extends ScrollPane {
    private final Label header = new Label("Your feed");
    private final ImageView logo = new ImageView();
    private final StackPane logoContainer = new StackPane();
    private VBox contentBox;

    public FeedPanel() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/feed.css")).toExternalForm());
        getStyleClass().add("feed-panel");
        setupLogo();

        contentBox = new VBox();
        contentBox.getStyleClass().add("feed-content");
        contentBox.setSpacing(20);

        setContent(contentBox);
        setFitToWidth(true);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        setupLayout();
    }

    private void setupLayout() {
        Region firstRowSpacer = new Region();
        HBox.setHgrow(firstRowSpacer, Priority.ALWAYS);
        header.getStyleClass().add("feed-header");
        HBox firstRow = new HBox(header, firstRowSpacer, logoContainer);
        addToFeed(firstRow);

        Label trendingNowLabel = new Label("Trending now");
        trendingNowLabel.getStyleClass().add("feed-trending-label");
        HBox secondRow = new HBox(trendingNowLabel);
        addToFeed(secondRow);

        TrackCard trackCard = new TrackCard(0L, "The Fate of Ophelia", "Taylor Swift", "cover-ophelia.png");
        FlowPane thirdRow = new FlowPane(trackCard);
        addToFeed(thirdRow);
    }

    private void setupLogo() {
        Image logo = new Image(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/assets/img/logo.png")).toExternalForm());
        this.logo.setSmooth(true);
        this.logo.setImage(logo);
        this.logo.setFitWidth(logo.getWidth() / 2);
        this.logo.setFitHeight(logo.getHeight() / 2);
        logoContainer.getStyleClass().add("feed-logo-container");
        logoContainer.getChildren().add(this.logo);
    }

    public void addToFeed(Node node) {
        contentBox.getChildren().add(node);
    }

    public VBox getContentBox() {
        return contentBox;
    }
}
