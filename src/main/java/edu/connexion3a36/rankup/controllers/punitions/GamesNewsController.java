package edu.connexion3a36.rankup.controllers.punitions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import edu.connexion3a36.rankup.config.GamesNewsConfig;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class GamesNewsController {

    @FXML private VBox newsContainer;
    @FXML private Label statusLabel;

    @FXML
    void initialize() {
        loadNews();
    }

    @FXML
    void onRefresh() {
        loadNews();
    }

    private void loadNews() {
        statusLabel.setText("Fetching latest eSports headlines...");
        newsContainer.getChildren().clear();
        newsContainer.getChildren().add(statusLabel);

        new Thread(() -> {
            try {
                List<NewsItem> news = fetchNewsFromApi();

                Platform.runLater(() -> {
                    newsContainer.getChildren().clear();
                    if (news.isEmpty()) {
                        statusLabel.setText("No news found or API error.");
                        newsContainer.getChildren().add(statusLabel);
                    } else {
                        for (NewsItem item : news) {
                            newsContainer.getChildren().add(createEnhancedNewsCard(item));
                        }
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    private List<NewsItem> fetchNewsFromApi() throws IOException, ParseException {
        List<NewsItem> newsList = new ArrayList<>();
        
        if (!GamesNewsConfig.isConfigured()) {
            return getMockNews();
        }

        String url = GamesNewsConfig.API_URL + "&apiKey=" + GamesNewsConfig.API_KEY;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                
                Gson gson = new Gson();
                JsonObject root = gson.fromJson(jsonResponse, JsonObject.class);
                
                if (root.has("status") && "ok".equals(root.get("status").getAsString())) {
                    JsonArray articles = root.getAsJsonArray("articles");
                    for (JsonElement element : articles) {
                        JsonObject article = element.getAsJsonObject();
                        
                        String title = article.has("title") ? article.get("title").getAsString() : "No Title";
                        String description = article.has("description") && !article.get("description").isJsonNull() 
                                            ? article.get("description").getAsString() : "";
                        String source = article.has("source") ? 
                                        article.getAsJsonObject("source").get("name").getAsString() : "Unknown Source";
                        String date = article.has("publishedAt") ? article.get("publishedAt").getAsString() : "";
                        String link = article.has("url") ? article.get("url").getAsString() : "";
                        String imageUrl = article.has("urlToImage") && !article.get("urlToImage").isJsonNull() 
                                         ? article.get("urlToImage").getAsString() : null;
                        
                        if (date.length() > 10) {
                            date = date.substring(0, 10);
                        }

                        newsList.add(new NewsItem(title, source, date, description, link, imageUrl));
                    }
                } else if (root.has("message")) {
                    String errorMsg = root.get("message").getAsString();
                    Platform.runLater(() -> statusLabel.setText("API Error: " + errorMsg));
                }
            }
        }
        
        return newsList;
    }

    private VBox createEnhancedNewsCard(NewsItem item) {
        VBox card = new VBox(0);
        card.getStyleClass().add("news-card");
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12; -fx-border-color: #334155; -fx-border-width: 1; -fx-border-radius: 12; -fx-overflow: hidden;");
        
        // Image Header (if available)
        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            try {
                ImageView imageView = new ImageView(new Image(item.imageUrl, 560, 200, true, true, true));
                imageView.setFitWidth(530);
                imageView.setFitHeight(180);
                imageView.setPreserveRatio(true);
                
                // Clip image to rounded corners
                Rectangle clip = new Rectangle(530, 180);
                clip.setArcWidth(24);
                clip.setArcHeight(24);
                imageView.setClip(clip);
                
                StackPane imageContainer = new StackPane(imageView);
                imageContainer.setPadding(new Insets(10, 10, 0, 10));
                card.getChildren().add(imageContainer);
            } catch (Exception ignored) {
                // Skip image if loading fails
            }
        }

        // Content Area
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        
        HBox meta = new HBox(10);
        meta.setAlignment(Pos.CENTER_LEFT);
        Label sourceLabel = new Label(item.source.toUpperCase());
        sourceLabel.setStyle("-fx-text-fill: #38bdf8; -fx-font-weight: bold; -fx-font-size: 10px; -fx-background-color: rgba(56, 189, 248, 0.1); -fx-padding: 2 6; -fx-background-radius: 4;");
        
        Label dateLabel = new Label(item.date);
        dateLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        meta.getChildren().addAll(sourceLabel, dateLabel);

        Label titleLabel = new Label(item.title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #f8fafc;");
        titleLabel.setWrapText(true);

        Label descriptionLabel = new Label(item.description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 13px; -fx-line-spacing: 1.4;");
        descriptionLabel.setMaxHeight(60);

        HBox actions = new HBox();
        actions.setAlignment(Pos.CENTER_RIGHT);
        Button readMoreBtn = new Button("Read Full Article →");
        readMoreBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #38bdf8; -fx-font-weight: bold; -fx-padding: 5 0; -fx-cursor: hand;");
        readMoreBtn.setOnAction(e -> openUrl(item.link));
        actions.getChildren().add(readMoreBtn);

        content.getChildren().addAll(meta, titleLabel, descriptionLabel, actions);
        card.getChildren().add(content);

        // Hover Effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #243146; -fx-background-radius: 12; -fx-border-color: #38bdf8; -fx-border-width: 1; -fx-border-radius: 12;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12; -fx-border-color: #334155; -fx-border-width: 1; -fx-border-radius: 12;"));

        return card;
    }

    private void openUrl(String url) {
        if (url == null || url.isEmpty()) return;
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<NewsItem> getMockNews() {
        List<NewsItem> news = new ArrayList<>();
        news.add(new NewsItem(
            "Welcome to RankUp ESports Hub",
            "SYSTEM",
            "2026-05-06",
            "This is your premium news feed. Configure your NewsAPI key in GamesNewsConfig.java to see real-time headlines.",
            "https://newsapi.org",
            null
        ));
        return news;
    }

    private static class NewsItem {
        String title;
        String source;
        String date;
        String description;
        String link;
        String imageUrl;

        NewsItem(String title, String source, String date, String description, String link, String imageUrl) {
            this.title = title;
            this.source = source;
            this.date = date;
            this.description = description;
            this.link = link;
            this.imageUrl = imageUrl;
        }
    }
}
