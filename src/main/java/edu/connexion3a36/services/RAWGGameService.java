package edu.connexion3a36.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.connexion3a36.entities.Game;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for interacting with RAWG Video Games API
 * API Documentation: https://rawg.io/
 */
public class RAWGGameService {
    private static final String RAWG_API_BASE_URL = "https://api.rawg.io/api";
    private static final String API_KEY = getApiKeyFromConfig();
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Get API key from environment variables or config
     */
    private static String getApiKeyFromConfig() {
        String apiKey = System.getenv("RAWG_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            apiKey = "14f9f7e72508416cb8d4c0ea21607d36"; // Fallback for development
            System.out.println("WARNING: RAWG_API_KEY not set in environment. Using fallback key.");
        }
        return apiKey;
    }

    /**
     * Search for games by name
     * @param searchQuery The game name to search for
     * @return List of Game objects matching the query
     */
    public List<Game> searchGames(String searchQuery) {
        List<Game> games = new ArrayList<>();

        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return games;
        }

        try {
            String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
            String url = RAWG_API_BASE_URL + "/games?search=" + encodedQuery + "&key=" + API_KEY;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                games = parseGamesResponse(response.body());
            } else {
                System.err.println("API ERROR: " + response.statusCode() + " - " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("ERROR searching games: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        return games;
    }

    /**
     * Get details of a specific game by ID
     * @param gameId The RAWG game ID
     * @return Game object with detailed information
     */
    public Game getGameDetails(long gameId) {
        try {
            String url = RAWG_API_BASE_URL + "/games/" + gameId + "?key=" + API_KEY;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseGameDetail(response.body());
            } else {
                System.err.println("API ERROR: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("ERROR fetching game details: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        return null;
    }

    /**
     * Get popular games (games with highest ratings)
     * @param limit Maximum number of games to return
     * @return List of popular Game objects
     */
    public List<Game> getPopularGames(int limit) {
        List<Game> games = new ArrayList<>();

        try {
            String url = RAWG_API_BASE_URL + "/games?ordering=-rating&page_size=" + limit + "&key=" + API_KEY;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                games = parseGamesResponse(response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("ERROR fetching popular games: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        return games;
    }

    /**
     * Parse games list from API response
     */
    private List<Game> parseGamesResponse(String jsonResponse) {
        List<Game> games = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode results = root.path("results");

            if (results.isArray()) {
                for (JsonNode element : results) {
                    Game game = parseGameObject(element);
                    if (game != null) {
                        games.add(game);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR parsing games response: " + e.getMessage());
        }

        return games;
    }

    /**
     * Parse individual game detail from API response
     */
    private Game parseGameDetail(String jsonResponse) {
        try {
            JsonNode gameObj = objectMapper.readTree(jsonResponse);
            return parseGameObject(gameObj);
        } catch (Exception e) {
            System.err.println("ERROR parsing game detail: " + e.getMessage());
        }
        return null;
    }

    /**
     * Parse a game object from JSON
     */
    private Game parseGameObject(JsonNode gameObj) {
        try {
            long id = gameObj.path("id").asLong(0L);
            String name = gameObj.path("name").asText("Unknown");
            String description = gameObj.path("description").asText("");
            double rating = gameObj.path("rating").asDouble(0.0);
            int reviewCount = gameObj.path("reviews_count").asInt(0);
            String backgroundImage = gameObj.path("background_image").asText("");
            String url = gameObj.path("url").asText("");
            String releaseDate = gameObj.path("released").asText("N/A");

            // Extract genres
            String[] genres = extractGenres(gameObj);

            // Extract platforms
            String[] platforms = extractPlatforms(gameObj);

            return new Game(id, name, description, rating, reviewCount, 
                    backgroundImage, url, releaseDate, genres, platforms);
        } catch (Exception e) {
            System.err.println("ERROR parsing game object: " + e.getMessage());
        }
        return null;
    }

    /**
     * Extract genres from game object
     */
    private String[] extractGenres(JsonNode gameObj) {
        try {
            if (gameObj.has("genres")) {
                JsonNode genresArray = gameObj.get("genres");
                String[] genres = new String[genresArray.size()];
                for (int i = 0; i < genresArray.size(); i++) {
                    genres[i] = genresArray.get(i).get("name").asText();
                }
                return genres;
            }
        } catch (Exception e) {
            System.err.println("ERROR extracting genres: " + e.getMessage());
        }
        return new String[0];
    }

    /**
     * Extract platforms from game object
     */
    private String[] extractPlatforms(JsonNode gameObj) {
        try {
            if (gameObj.has("platforms")) {
                JsonNode platformsArray = gameObj.get("platforms");
                String[] platforms = new String[platformsArray.size()];
                for (int i = 0; i < platformsArray.size(); i++) {
                    platforms[i] = platformsArray.get(i).get("platform").get("name").asText();
                }
                return platforms;
            }
        } catch (Exception e) {
            System.err.println("ERROR extracting platforms: " + e.getMessage());
        }
        return new String[0];
    }

    /**
     * Check if API key is configured
     */
    public static boolean isApiKeyConfigured() {
        return API_KEY != null && !API_KEY.isEmpty();
    }
}
