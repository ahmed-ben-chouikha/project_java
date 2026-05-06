package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.Game;
import edu.connexion3a36.services.RAWGGameService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for game search functionality using RAWG API
 */
public class GameSearchController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private Button popularBtn;
    @FXML private Label loadingLabel;
    @FXML private VBox resultsContainer;
    @FXML private Label noResultsLabel;

    private RAWGGameService gameService;
    private static final int POPULAR_GAMES_LIMIT = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameService = new RAWGGameService();
        
        // Display initial message
        loadingLabel.setText("Ready to search! Enter a game name or click 'Popular Games'");
        loadingLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12;");
    }

    /**
     * Handle search button click or Enter key in search field
     */
    @FXML
    public void onSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            showError("Please enter a game name");
            return;
        }

        performSearch(query);
    }

    /**
     * Handle Enter key in search field
     */
    @FXML
    public void onSearchKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onSearch();
        }
    }

    /**
     * Load and display popular games
     */
    @FXML
    public void onLoadPopular() {
        loadingLabel.setText("Loading popular games...");
        loadingLabel.setStyle("-fx-text-fill: #FFA500;");
        
        // Run API call in background thread to avoid freezing UI
        Thread searchThread = new Thread(() -> {
            try {
                List<Game> games = gameService.getPopularGames(POPULAR_GAMES_LIMIT);
                Platform.runLater(() -> displayResults(games, "Popular Games"));
            } catch (Exception e) {
                Platform.runLater(() -> showError("Failed to load popular games: " + e.getMessage()));
            }
        });
        searchThread.setDaemon(true);
        searchThread.start();
    }

    /**
     * Perform search with the given query
     */
    private void performSearch(String query) {
        loadingLabel.setText("Searching for \"" + query + "\"...");
        loadingLabel.setStyle("-fx-text-fill: #FFA500;");
        resultsContainer.getChildren().clear();
        
        // Run API call in background thread
        Thread searchThread = new Thread(() -> {
            try {
                List<Game> results = gameService.searchGames(query);
                Platform.runLater(() -> displayResults(results, "Search Results: " + query));
            } catch (Exception e) {
                Platform.runLater(() -> showError("Search failed: " + e.getMessage()));
            }
        });
        searchThread.setDaemon(true);
        searchThread.start();
    }

    /**
     * Display search results in the results container
     */
    private void displayResults(List<Game> games, String title) {
        resultsContainer.getChildren().clear();
        
        if (games == null || games.isEmpty()) {
            loadingLabel.setText("No games found. Try a different search!");
            loadingLabel.setStyle("-fx-text-fill: #888888;");
            noResultsLabel.setVisible(true);
            return;
        }

        loadingLabel.setText("Found " + games.size() + " game(s)");
        loadingLabel.setStyle("-fx-text-fill: #4CAF50;");
        noResultsLabel.setVisible(false);

        // Display each game
        for (Game game : games) {
            resultsContainer.getChildren().add(createGameCard(game));
        }
    }

    /**
     * Create a card UI component for each game
     */
    private VBox createGameCard(Game game) {
        VBox card = new VBox();
        card.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
        card.setPadding(new Insets(12));
        card.setSpacing(8);

        // Game Title
        Label titleLabel = new Label(game.getName());
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Rating and Review Count
        HBox ratingBox = new HBox();
        ratingBox.setSpacing(8);
        ratingBox.setAlignment(Pos.CENTER_LEFT);
        
        Label ratingLabel = new Label("⭐ " + String.format("%.1f", game.getRating()) + "/5");
        ratingLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #FF9800;");
        
        Label reviewCountLabel = new Label("(" + game.getReviewCount() + " reviews)");
        reviewCountLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
        
        ratingBox.getChildren().addAll(ratingLabel, reviewCountLabel);

        // Release Date
        Label dateLabel = new Label("Release Date: " + game.getReleaseDate());
        dateLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");

        // Genres
        String genresText = String.join(", ", game.getGenres());
        Label genresLabel = new Label("Genres: " + (genresText.isEmpty() ? "N/A" : genresText));
        genresLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
        genresLabel.setWrapText(true);

        // Platforms
        String platformsText = String.join(", ", game.getPlatforms());
        Label platformsLabel = new Label("Platforms: " + (platformsText.isEmpty() ? "N/A" : platformsText));
        platformsLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
        platformsLabel.setWrapText(true);

        // Description (truncated)
        if (!game.getDescription().isEmpty()) {
            String description = game.getDescription().length() > 200 ?
                    game.getDescription().substring(0, 200) + "..." :
                    game.getDescription();
            Label descLabel = new Label(description);
            descLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #555;");
            descLabel.setWrapText(true);
            card.getChildren().add(descLabel);
        }

        // Action Buttons
        HBox buttonsBox = new HBox();
        buttonsBox.setSpacing(8);
        buttonsBox.setAlignment(Pos.CENTER_LEFT);

        Button detailsBtn = new Button("View Details");
        detailsBtn.setStyle("-fx-padding: 6 12; -fx-font-size: 11; -fx-background-color: #2196F3; -fx-text-fill: white;");
        detailsBtn.setOnAction(event -> openGameDetails(game));

        Button linkBtn = new Button("Open on RAWG");
        linkBtn.setStyle("-fx-padding: 6 12; -fx-font-size: 11; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        linkBtn.setOnAction(event -> openGameLink(game));

        buttonsBox.getChildren().addAll(detailsBtn, linkBtn);

        // Add all elements to card
        card.getChildren().addAll(
                titleLabel,
                ratingBox,
                dateLabel,
                genresLabel,
                platformsLabel,
                buttonsBox
        );

        return card;
    }

    /**
     * Open game details (in a real app, would open a detail view)
     */
    private void openGameDetails(Game game) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Details: " + game.getName());
        alert.setHeaderText(game.getName());
        
        String details = "ID: " + game.getId() + "\n" +
                "Rating: " + String.format("%.1f/5", game.getRating()) + "\n" +
                "Release Date: " + game.getReleaseDate() + "\n" +
                "Review Count: " + game.getReviewCount() + "\n\n" +
                "Description:\n" + (game.getDescription().isEmpty() ? "No description available" : game.getDescription());
        
        alert.setContentText(details);
        alert.showAndWait();
    }

    /**
     * Open game link on RAWG website
     */
    private void openGameLink(Game game) {
        if (game.getRawgUrl() == null || game.getRawgUrl().isEmpty()) {
            showError("No link available for this game");
            return;
        }

        try {
            // Try to open link in default browser
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(game.getRawgUrl()));
            } else {
                // Fallback: show link in alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Game Link");
                alert.setHeaderText("Visit Game Page");
                TextArea textArea = new TextArea(game.getRawgUrl());
                textArea.setEditable(false);
                textArea.setWrapText(true);
                alert.getDialogPane().setContent(textArea);
                alert.showAndWait();
            }
        } catch (Exception e) {
            showError("Failed to open link: " + e.getMessage());
        }
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        loadingLabel.setText("❌ " + message);
        loadingLabel.setStyle("-fx-text-fill: #F44336;");
    }
}
