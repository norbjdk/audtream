package com.audtream.desktop.controller;

import com.audtream.desktop.model.dto.TrackResponse;
import com.audtream.desktop.service.CurrentUserService;
import com.audtream.desktop.service.TrackService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DiscoverController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ScrollPane mainScrollPane;
    @FXML private VBox mainContainer;
    @FXML private ProgressIndicator loadingIndicator;

    private TrackService trackService;
    private List<TrackResponse> allTracks = new ArrayList<>();

    private String[] genres = {
            "All", "Rock", "Pop", "Hip Hop", "Jazz", "Electronic",
            "R&B", "Metal", "Classical", "Country", "Folk", "Blues",
            "Reggae", "Punk", "Funk", "Soul", "Disco", "Techno",
            "House", "Trance", "Drum & Bass", "Dubstep", "Trap", "Lo-fi"
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        trackService = new TrackService();

        setupUI();
        loadContent();
    }

    private void setupUI() {

        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterTracks(newVal);
        });
    }

    private void loadContent() {
        showLoading(true);
        mainContainer.getChildren().clear();

        new Thread(() -> {
            try {
                allTracks = trackService.getAllTracks(null, "newest");

                List<TrackResponse> recommended = trackService.getRecommendedTracks(10);

                List<TrackResponse> newReleases = trackService.getNewReleases(10);

                List<TrackResponse> topTracks = trackService.getTopTracks(10);

                Platform.runLater(() -> {
                    showLoading(false);
                    buildDiscoverView(recommended, newReleases, topTracks);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load content: " + e.getMessage());
                });
            }
        }).start();
    }

    private void buildDiscoverView(List<TrackResponse> recommended,
                                   List<TrackResponse> newReleases,
                                   List<TrackResponse> topTracks) {
        mainContainer.getChildren().clear();

        VBox heroSection = createHeroSection();
        mainContainer.getChildren().add(heroSection);

        if (!recommended.isEmpty()) {
            VBox recommendedSection = createSection("🔥 Recommended for You", recommended, "#ef4444");
            mainContainer.getChildren().add(recommendedSection);
        }

        if (!newReleases.isEmpty()) {
            VBox newReleasesSection = createSection("🆕 New Releases", newReleases, "#10b981");
            mainContainer.getChildren().add(newReleasesSection);
        }

        if (!topTracks.isEmpty()) {
            VBox topTracksSection = createSection("📈 Top Tracks", topTracks, "#8b5cf6");
            mainContainer.getChildren().add(topTracksSection);
        }

        VBox genresSection = createGenresSection();
        mainContainer.getChildren().add(genresSection);

        VBox allTracksSection = createAllTracksSection();
        mainContainer.getChildren().add(allTracksSection);
    }

    private VBox createHeroSection() {
        VBox hero = new VBox(15);
        hero.setPadding(new Insets(40, 30, 40, 30));
        hero.setStyle(
                "-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);" +
                        "-fx-background-radius: 20;"
        );

        Label title = new Label("Discover Music");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 42px; -fx-font-weight: bold;");

        Label subtitle = new Label("Explore new tracks, artists, and genres");
        subtitle.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 18px;");

        HBox stats = new HBox(30);
        stats.setAlignment(Pos.CENTER_LEFT);

        VBox totalTracksBox = createStatBox(String.valueOf(allTracks.size()), "Total Tracks");
        VBox genresBox = createStatBox(String.valueOf(getUniqueGenres().size()), "Genres");
        VBox artistsBox = createStatBox(String.valueOf(getUniqueArtists().size()), "Artists");

        stats.getChildren().addAll(totalTracksBox, genresBox, artistsBox);

        hero.getChildren().addAll(title, subtitle, stats);

        VBox.setMargin(hero, new Insets(0, 0, 20, 0));
        return hero;
    }

    private VBox createStatBox(String value, String label) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER_LEFT);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");

        Label textLabel = new Label(label);
        textLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 14px;");

        box.getChildren().addAll(valueLabel, textLabel);
        return box;
    }

    private VBox createSection(String title, List<TrackResponse> tracks, String accentColor) {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20, 0, 20, 0));

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 0 0 10 0;"
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        HBox tracksBox = new HBox(15);
        tracksBox.setPadding(new Insets(10));

        for (TrackResponse track : tracks) {
            VBox card = createTrackCard(track, accentColor);
            tracksBox.getChildren().add(card);
        }

        scrollPane.setContent(tracksBox);

        section.getChildren().addAll(titleLabel, scrollPane);
        return section;
    }

    private VBox createTrackCard(TrackResponse track, String accentColor) {
        VBox card = new VBox(10);
        card.setPrefSize(180, 240);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: #1a1a1a;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;"
        );

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #252525;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, " + accentColor + ", 10, 0.3, 0, 0);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: #1a1a1a;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;"
        ));

        StackPane coverPane = new StackPane();
        coverPane.setPrefSize(150, 150);
        coverPane.setStyle(
                "-fx-background-color: linear-gradient(135deg, " + accentColor + " 0%, #ec4899 100%);" +
                        "-fx-background-radius: 12;"
        );

        if (track.getCoverUrl() != null && !track.getCoverUrl().isEmpty()) {
            try {
                ImageView cover = new ImageView(new Image(track.getCoverUrl(), true));
                cover.setFitWidth(150);
                cover.setFitHeight(150);
                cover.setPreserveRatio(false);
                cover.setStyle("-fx-background-radius: 12;");
                coverPane.getChildren().add(cover);
            } catch (Exception e) {
                Label icon = new Label("🎵");
                icon.setStyle("-fx-font-size: 48px;");
                coverPane.getChildren().add(icon);
            }
        } else {
            Label icon = new Label("🎵");
            icon.setStyle("-fx-font-size: 48px;");
            coverPane.getChildren().add(icon);
        }

        Button playButton = new Button("▶");
        playButton.setStyle(
                "-fx-background-color: " + accentColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 20px;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-min-width: 50px;" +
                        "-fx-min-height: 50px;" +
                        "-fx-cursor: hand;"
        );
        playButton.setVisible(false);
        playButton.setOnAction(e -> handlePlay(track));

        coverPane.getChildren().add(playButton);
        StackPane.setAlignment(playButton, Pos.CENTER);

        card.setOnMouseEntered(e -> playButton.setVisible(true));
        card.setOnMouseExited(e -> playButton.setVisible(false));

        Label titleLabel = new Label(track.getTitle());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(150);

        Label artistLabel = new Label(track.getArtist());
        artistLabel.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");
        artistLabel.setWrapText(true);
        artistLabel.setMaxWidth(150);

        card.getChildren().addAll(coverPane, titleLabel, artistLabel);

        return card;
    }

    private VBox createGenresSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20, 0, 20, 0));

        Label titleLabel = new Label("🎼 Browse by Genre");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        FlowPane genresFlow = new FlowPane(10, 10);
        genresFlow.setPadding(new Insets(10));

        String[] colors = {
                "#ef4444", "#f59e0b", "#10b981", "#3b82f6",
                "#8b5cf6", "#ec4899", "#06b6d4", "#84cc16"
        };

        for (int i = 0; i < genres.length; i++) {
            String genre = genres[i];
            if (genre.equals("All")) continue;

            String color = colors[i % colors.length];
            Button genreButton = createGenreButton(genre, color);
            genresFlow.getChildren().add(genreButton);
        }

        section.getChildren().addAll(titleLabel, genresFlow);
        return section;
    }

    private Button createGenreButton(String genre, String color) {
        Button button = new Button(genre);
        button.setPrefSize(140, 140);
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-scale-x: 1.05;" +
                        "-fx-scale-y: 1.05;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;"
        ));

        button.setOnAction(e -> filterByGenre(genre));

        return button;
    }

    private VBox createAllTracksSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20, 0, 20, 0));

        Label titleLabel = new Label("🎵 All Tracks");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));

        int col = 0;
        int row = 0;
        int maxPerRow = 5;

        for (TrackResponse track : allTracks.stream().limit(20).collect(Collectors.toList())) {
            VBox card = createTrackCard(track, "#8b5cf6");
            grid.add(card, col, row);

            col++;
            if (col >= maxPerRow) {
                col = 0;
                row++;
            }
        }

        section.getChildren().addAll(titleLabel, grid);
        return section;
    }

    private void filterTracks(String searchTerm) {
        // TODO: Implement search filtering
        System.out.println("Searching for: " + searchTerm);
    }

    private void filterByGenre(String genre) {
        showLoading(true);
        new Thread(() -> {
            try {
                List<TrackResponse> filtered = trackService.getAllTracks(genre, "popular");
                Platform.runLater(() -> {
                    showLoading(false);
                    showGenreResults(genre, filtered);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to filter by genre: " + e.getMessage());
                });
            }
        }).start();
    }

    private void showGenreResults(String genre, List<TrackResponse> tracks) {
        mainContainer.getChildren().clear();

        Button backButton = new Button("← Back to Discover");
        backButton.setStyle(
                "-fx-background-color: #2a2a2a;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        backButton.setOnAction(e -> loadContent());

        Label titleLabel = new Label(genre + " Music");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");

        Label countLabel = new Label(tracks.size() + " tracks found");
        countLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 16px;");

        VBox headerBox = new VBox(15, backButton, titleLabel, countLabel);
        headerBox.setPadding(new Insets(0, 0, 20, 0));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        int col = 0;
        int row = 0;
        int maxPerRow = 5;

        for (TrackResponse track : tracks) {
            VBox card = createTrackCard(track, "#8b5cf6");
            grid.add(card, col, row);

            col++;
            if (col >= maxPerRow) {
                col = 0;
                row++;
            }
        }

        mainContainer.getChildren().addAll(headerBox, grid);
    }

    private void handlePlay(TrackResponse track) {
        System.out.println("Playing: " + track.getTitle());
        // TODO: Integration with music player
    }

    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
        mainScrollPane.setVisible(!show);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Failed to load content");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Set<String> getUniqueGenres() {
        return allTracks.stream()
                .map(TrackResponse::getGenre)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<String> getUniqueArtists() {
        return allTracks.stream()
                .map(TrackResponse::getArtist)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}