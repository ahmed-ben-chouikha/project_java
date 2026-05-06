package edu.connexion3a36.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.connexion3a36.entities.PlayerCandidate;
import edu.connexion3a36.entities.Team;
import edu.connexion3a36.tools.MyConnection;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerScoutingService {

    private static final String DEFAULT_MODEL = "llama-3.1-70b-versatile";
    private static final String DEFAULT_ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public List<PlayerCandidate> getAvailablePlayers() throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        if (cnx == null) {
            throw new SQLException("Database connection is not available.");
        }

        List<PlayerCandidate> players = new ArrayList<>();
        String sql = "SELECT * FROM player WHERE (player_status IS NULL OR LOWER(player_status) = 'approved') ORDER BY created_at DESC, id DESC";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                players.add(mapCandidate(rs));
            }
        }
        return players;
    }

    public List<PlayerCandidate> recommendFormation(Team team, int formationSize) throws IOException, InterruptedException, SQLException {
        List<PlayerCandidate> availablePlayers = getAvailablePlayers();
        if (availablePlayers.isEmpty()) {
            return List.of();
        }

        String apiKey = resolveApiKey();
        if (apiKey.isBlank()) {
            return fallbackRecommendation(availablePlayers, formationSize);
        }

        String prompt = buildPrompt(team, availablePlayers, formationSize);
        String body = buildRequestBody(prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(resolveEndpoint()))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return fallbackRecommendation(availablePlayers, formationSize);
        }

        List<Integer> orderedIds = parseOrderedIds(response.body());
        Map<Integer, PlayerCandidate> byId = new LinkedHashMap<>();
        for (PlayerCandidate candidate : availablePlayers) {
            byId.put(candidate.getId(), candidate);
        }

        List<PlayerCandidate> selected = new ArrayList<>();
        for (Integer id : orderedIds) {
            PlayerCandidate candidate = byId.get(id);
            if (candidate != null && selected.stream().noneMatch(existing -> existing.getId() == candidate.getId())) {
                selected.add(candidate);
            }
            if (selected.size() >= formationSize) {
                break;
            }
        }

        if (selected.size() < formationSize) {
            for (PlayerCandidate candidate : availablePlayers) {
                if (selected.size() >= formationSize) {
                    break;
                }
                if (selected.stream().noneMatch(existing -> existing.getId() == candidate.getId())) {
                    selected.add(candidate);
                }
            }
        }

        return selected;
    }

    public List<PlayerCandidate> fallbackRecommendation(List<PlayerCandidate> players, int formationSize) {
        int size = Math.min(formationSize, players.size());
        return new ArrayList<>(players.subList(0, size));
    }

    public List<PlayerCandidate> searchPlayersByName(String searchQuery) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        if (cnx == null) {
            throw new SQLException("Database connection is not available.");
        }

        if (searchQuery == null || searchQuery.isBlank()) {
            return List.of();
        }

        List<PlayerCandidate> players = new ArrayList<>();
        String query = "%" + searchQuery.trim() + "%";
        String sql = "SELECT * FROM player WHERE (player_status IS NULL OR LOWER(player_status) = 'approved') " +
                     "AND (LOWER(nickname) LIKE LOWER(?) OR LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?)) " +
                     "ORDER BY first_name, last_name, nickname, id DESC LIMIT 50";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, query);
            pst.setString(2, query);
            pst.setString(3, query);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    players.add(mapCandidate(rs));
                }
            }
        }
        return players;
    }

    private String resolveApiKey() {
        String value = System.getProperty("groq.api.key");
        if (value == null || value.isBlank()) {
            value = System.getenv("GROQ_API_KEY");
        }
        return value == null ? "" : value.trim();
    }

    private String resolveEndpoint() {
        String value = System.getProperty("groq.endpoint");
        if (value == null || value.isBlank()) {
            value = System.getenv("GROQ_ENDPOINT");
        }
        return value == null || value.isBlank() ? DEFAULT_ENDPOINT : value.trim();
    }

    private String resolveModel() {
        String value = System.getProperty("groq.model");
        if (value == null || value.isBlank()) {
            value = System.getenv("GROQ_MODEL");
        }
        return value == null || value.isBlank() ? DEFAULT_MODEL : value.trim();
    }

    private String buildPrompt(Team team, List<PlayerCandidate> players, int formationSize) {
        StringBuilder builder = new StringBuilder();
        builder.append("You are selecting the best players for an esports team formation.\n")
                .append("Return only JSON in this exact shape: {\"playerIds\":[1,2,3,4,5], \"formation\":[{\"playerId\":1,\"position\":\"IGL\",\"reason\":\"...\"}]}\n")
                .append("Choose exactly ").append(formationSize).append(" players. Prefer approved players who are not already attached to another team.\n")
                .append("Team context:\n")
                .append("- teamName: ").append(team.getName()).append("\n")
                .append("- game: ").append(team.getJeu()).append("\n")
                .append("- level: ").append(team.getNiveau()).append("\n")
                .append("- score: ").append(team.getScore()).append("\n")
                .append("Available players:\n");

        for (PlayerCandidate player : players) {
            builder.append("- id=").append(player.getId())
                    .append(", name=").append(player.getDisplayName())
                    .append(", nickname=").append(safe(player.getNickname()))
                    .append(", role=").append(safe(player.getRole()))
                    .append(", status=").append(safe(player.getStatus()))
                    .append(", teamId=").append(player.getTeamId() == null ? "null" : player.getTeamId())
                    .append("\n");
        }
        return builder.toString();
    }

    private String buildRequestBody(String prompt) throws IOException {
        return "{" +
                "\"model\":\"" + escapeJson(resolveModel()) + "\"," +
                "\"messages\":[{" +
                    "\"role\":\"system\",\"content\":\"You are a scouting assistant for esports rosters.\"" +
                "},{" +
                    "\"role\":\"user\",\"content\":\"" + escapeJson(prompt) + "\"" +
                "}]," +
                "\"temperature\":0.2," +
                "\"max_tokens\":512" +
                "}";
    }

    private List<Integer> parseOrderedIds(String responseBody) throws IOException {
        JsonNode root = MAPPER.readTree(responseBody);
        String content = root.path("choices").path(0).path("message").path("content").asText("");
        if (content.isBlank()) {
            return List.of();
        }

        JsonNode parsed = tryParseJson(content);
        if (parsed != null) {
            JsonNode idsNode = parsed.path("playerIds");
            if (idsNode.isArray()) {
                List<Integer> ids = new ArrayList<>();
                idsNode.forEach(node -> {
                    if (node.canConvertToInt()) {
                        ids.add(node.asInt());
                    }
                });
                return ids;
            }
        }

        List<Integer> ids = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            if (Character.isDigit(ch)) {
                current.append(ch);
            } else if (current.length() > 0) {
                ids.add(Integer.parseInt(current.toString()));
                current.setLength(0);
            }
        }
        if (current.length() > 0) {
            ids.add(Integer.parseInt(current.toString()));
        }
        return ids;
    }

    private JsonNode tryParseJson(String content) {
        try {
            return MAPPER.readTree(content);
        } catch (Exception ignored) {
            return null;
        }
    }

    private PlayerCandidate mapCandidate(ResultSet rs) throws SQLException {
        Set<String> columns = getColumnNames(rs);
        int id = rs.getInt("id");
        String nickname = getString(rs, columns, "nickname");
        String firstName = getString(rs, columns, "first_name");
        String lastName = getString(rs, columns, "last_name");
        String role = getString(rs, columns, "role");
        String status = getString(rs, columns, "player_status");
        Integer teamId = columns.contains("team_id") && rs.getObject("team_id") != null ? rs.getInt("team_id") : null;
        String birthDate = columns.contains("birth_date") ? safeDate(rs.getString("birth_date")) : null;
        String createdAt = columns.contains("created_at") ? rs.getString("created_at") : null;

        String displayName = buildDisplayName(nickname, firstName, lastName, id);
        return new PlayerCandidate(id, displayName, nickname, firstName, lastName, role, status, teamId, birthDate, createdAt);
    }

    private String buildDisplayName(String nickname, String firstName, String lastName, int id) {
        StringBuilder builder = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) {
            builder.append(firstName.trim());
        }
        if (lastName != null && !lastName.isBlank()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(lastName.trim());
        }
        if (builder.length() > 0 && nickname != null && !nickname.isBlank()) {
            builder.append(" (@").append(nickname.trim()).append(")");
        } else if (builder.length() == 0 && nickname != null && !nickname.isBlank()) {
            builder.append(nickname.trim());
        }
        if (builder.length() == 0) {
            builder.append("Player #").append(id);
        }
        return builder.toString();
    }

    private Set<String> getColumnNames(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        Set<String> names = new HashSet<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            names.add(metaData.getColumnLabel(i).toLowerCase());
        }
        return names;
    }

    private String getString(ResultSet rs, Set<String> columns, String key) throws SQLException {
        if (!columns.contains(key.toLowerCase())) {
            return null;
        }
        return rs.getString(key);
    }

    private String safeDate(String value) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value.substring(0, 10)).toString();
        } catch (Exception e) {
            return value;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"' -> builder.append("\\\"");
                case '\\' -> builder.append("\\\\");
                case '\b' -> builder.append("\\b");
                case '\f' -> builder.append("\\f");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '\t' -> builder.append("\\t");
                default -> builder.append(ch);
            }
        }
        return builder.toString();
    }
}
