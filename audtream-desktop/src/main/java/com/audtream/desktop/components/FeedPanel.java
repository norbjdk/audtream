package com.audtream.desktop.components;

import com.audtream.desktop.manager.AppStateManager;
import com.audtream.desktop.model.dto.TrackDTO;
import com.audtream.desktop.util.DataBindingHelper;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.Objects;

public class FeedPanel extends ScrollPane {
    private final Label header = new Label("Your feed");
    private final ImageView logo = new ImageView();
    private final StackPane logoContainer = new StackPane();
    private VBox contentBox;
    private final DataBindingHelper binder = new DataBindingHelper();
    private final AppStateManager app = AppStateManager.getInstance();

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
        loadData();
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
    }

    private void loadData() {
        binder.bindTopTracks(
                tracks -> {
                    Platform.runLater(() -> {
                        FlowPane tracksRow = new FlowPane();
                        tracksRow.setHgap(10);
                        tracksRow.setVgap(10);

                        for (TrackDTO track : tracks) {
                            TrackCard trackCard = new TrackCard(
                                    track.getId(),
                                    track.getTitle(),
                                    track.getArtist(),
                                    track.getCoverUrl()
                            );

                            trackCard.setOnMouseClicked(event -> handleTrackClick(track));
                            tracksRow.getChildren().add(trackCard);
                        }

                        addToFeed(tracksRow);
                    });
                },
                error -> {
                }
        );

        binder.bindNewReleases(
                tracks -> {
                    Platform.runLater(() -> {
                        Label newReleasesLabel = new Label("New Releases");
                        newReleasesLabel.getStyleClass().add("feed-trending-label");
                        HBox labelRow = new HBox(newReleasesLabel);
                        addToFeed(labelRow);

                        FlowPane tracksRow = new FlowPane();
                        tracksRow.setHgap(10);
                        tracksRow.setVgap(10);

                        for (TrackDTO track : tracks) {
                            TrackCard trackCard = new TrackCard(
                                    track.getId(),
                                    track.getTitle(),
                                    track.getArtist(),
                                    track.getCoverUrl()
                            );

                            trackCard.setOnMouseClicked(event -> handleTrackClick(track));
                            tracksRow.getChildren().add(trackCard);
                        }

                        addToFeed(tracksRow);
                    });
                },
                error -> {
                }
        );
    }

    private void handleTrackClick(TrackDTO track) {
        new Thread(() -> {
            try {
                app.playTrack(track);
            } catch (Exception e) {
            }
        }).start();
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