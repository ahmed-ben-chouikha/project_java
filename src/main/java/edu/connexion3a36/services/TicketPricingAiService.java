package edu.connexion3a36.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.connexion3a36.entities.Ticket;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public class TicketPricingAiService {

    private static final String DEFAULT_MODEL = "llama-3.1-8b-instant";
    private static final String DEFAULT_ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public double suggestPrice(Ticket ticket) throws IOException, InterruptedException {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket is required.");
        }

        String apiKey = resolveApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Groq API key is missing. Set GROQ_API_KEY or -Dgroq.api.key.");
        }

        double currentPrice = ticket.getPrice();
        int quantity = ticket.getQuantity();
        int sold = ticket.getSold();
        int remaining = Math.max(0, quantity - sold);
        double soldRatio = quantity <= 0 ? 0.0 : (double) sold / (double) quantity;

        String prompt = buildPrompt(ticket, currentPrice, quantity, sold, remaining, soldRatio);
        String body = buildRequestBody(prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(resolveEndpoint()))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Groq returned status " + response.statusCode() + ": " + response.body());
        }

        return parseSuggestedPrice(response.body(), currentPrice);
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

    private String buildPrompt(Ticket ticket, double currentPrice, int quantity, int sold, int remaining, double soldRatio) {
        return "You are helping price esports tickets.\n"
                + "Return only JSON with this shape: {\"suggestedPrice\": number, \"reason\": string}.\n"
                + "Rules: suggestedPrice must be in the same currency as the current price, rounded to 2 decimals, and should stay within 15% of the current price unless the sales data strongly suggests a bigger move.\n"
                + "Use this ticket data:\n"
                + "- ticketId: " + ticket.getId() + "\n"
                + "- gameId: " + ticket.getGameId() + "\n"
                + "- ticketNumber: " + safe(ticket.getTicketNumber()) + "\n"
                + "- type: " + safe(ticket.getType()) + "\n"
                + "- currentPrice: " + DECIMAL_FORMAT.format(currentPrice) + "\n"
                + "- quantity: " + quantity + "\n"
                + "- sold: " + sold + "\n"
                + "- remaining: " + remaining + "\n"
                + "- sellThroughRate: " + DECIMAL_FORMAT.format(soldRatio * 100.0) + "%\n"
                + "Suggest a price based on sales momentum, remaining inventory, and perceived demand.";
    }

    private String buildRequestBody(String prompt) throws IOException {
        String system = "You are a pricing assistant for esports ticket inventory.";
        return "{" +
                "\"model\":\"" + escapeJson(resolveModel()) + "\"," +
                "\"messages\":[{" +
                    "\"role\":\"system\",\"content\":\"" + escapeJson(system) + "\"" +
                "},{" +
                    "\"role\":\"user\",\"content\":\"" + escapeJson(prompt) + "\"" +
                "}]," +
                "\"temperature\":0.2," +
                "\"max_tokens\":256" +
                "}";
    }

    private double parseSuggestedPrice(String responseBody, double fallbackPrice) throws IOException {
        JsonNode root = MAPPER.readTree(responseBody);
        JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
        String content = contentNode.isMissingNode() ? "" : contentNode.asText("");
        if (content.isBlank()) {
            return fallbackPrice;
        }

        JsonNode contentJson = tryParseJson(content);
        if (contentJson != null && contentJson.has("suggestedPrice")) {
            return sanitizePrice(contentJson.get("suggestedPrice").asDouble(fallbackPrice), fallbackPrice);
        }

        double extracted = extractFirstNumber(content, fallbackPrice);
        return sanitizePrice(extracted, fallbackPrice);
    }

    private JsonNode tryParseJson(String content) {
        try {
            return MAPPER.readTree(content);
        } catch (Exception ignored) {
            return null;
        }
    }

    private double extractFirstNumber(String text, double fallbackPrice) {
        StringBuilder builder = new StringBuilder();
        boolean seenDigit = false;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if ((ch >= '0' && ch <= '9') || ch == '.' || ch == ',') {
                builder.append(ch == ',' ? '.' : ch);
                seenDigit = true;
            } else if (seenDigit) {
                break;
            }
        }
        if (builder.length() == 0) {
            return fallbackPrice;
        }
        try {
            return Double.parseDouble(builder.toString());
        } catch (NumberFormatException e) {
            return fallbackPrice;
        }
    }

    private double sanitizePrice(double suggested, double currentPrice) {
        if (Double.isNaN(suggested) || Double.isInfinite(suggested) || suggested <= 0) {
            return currentPrice;
        }
        double lowerBound = currentPrice * 0.85;
        double upperBound = currentPrice * 1.15;
        if (suggested < lowerBound) {
            suggested = lowerBound;
        }
        if (suggested > upperBound) {
            suggested = upperBound;
        }
        return Math.round(suggested * 100.0) / 100.0;
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

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}