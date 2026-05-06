package edu.connexion3a36.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class BrevoMailService {

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    private static final Map<String, String> LOCAL_ENV = loadLocalEnv();

    private BrevoMailService() {
    }

    public static void sendEmail(String toEmail, String subject, String htmlContent, String textContent) throws Exception {
        String apiKey = resolveConfig("BREVO_API_KEY");
        if (apiKey.isBlank()) {
            throw new IllegalStateException("BREVO_API_KEY is not set.");
        }

        String jsonBody = buildBrevoPayload(toEmail, subject, htmlContent, textContent);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BREVO_API_URL))
                .header("accept", "application/json")
                .header("api-key", apiKey)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            System.err.println("Brevo send failed for " + toEmail + ": " + response.statusCode() + " - " + response.body());
            throw new RuntimeException("Brevo API error: " + response.statusCode() + " - " + response.body());
        }

        System.out.println("Email sent successfully via Brevo to: " + toEmail);
    }

    private static String buildBrevoPayload(String toEmail, String subject, String htmlContent, String textContent) {
        String from = resolveConfig("BREVO_FROM_EMAIL", "GMAIL_SMTP_FROM", "GMAIL_SMTP_USERNAME");
        if (from.isBlank()) {
            throw new IllegalStateException("No verified sender configured. Set BREVO_FROM_EMAIL (or GMAIL_SMTP_FROM) to a Brevo-verified sender address.");
        }

        if (toEmail == null || toEmail.isBlank()) {
            throw new IllegalStateException("Target email address is empty.");
        }

        return String.format(
                "{\"sender\":{\"name\":\"RankUp\",\"email\":\"%s\"},\"to\":[{\"email\":\"%s\"}],\"subject\":\"%s\",\"htmlContent\":\"%s\",\"textContent\":\"%s\"}",
                escapeJsonString(from),
                escapeJsonString(toEmail),
                escapeJsonString(subject),
                escapeJsonString(htmlContent != null ? htmlContent : ""),
                escapeJsonString(textContent != null ? textContent : "")
        );
    }

    private static String resolveConfig(String key) {
        return resolveConfig(key, new String[0]);
    }

    private static String resolveConfig(String key, String... fallbacks) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = System.getProperty(key);
        }
        if ((value == null || value.isBlank()) && LOCAL_ENV.containsKey(key)) {
            value = LOCAL_ENV.get(key);
        }
        if ((value == null || value.isBlank()) && fallbacks != null) {
            for (String fallback : fallbacks) {
                if (fallback == null || fallback.isBlank()) {
                    continue;
                }
                value = System.getenv(fallback);
                if (value == null || value.isBlank()) {
                    value = System.getProperty(fallback);
                }
                if ((value == null || value.isBlank()) && LOCAL_ENV.containsKey(fallback)) {
                    value = LOCAL_ENV.get(fallback);
                }
                if (value != null && !value.isBlank()) {
                    break;
                }
            }
        }
        return value == null ? "" : value.trim();
    }

    private static Map<String, String> loadLocalEnv() {
        Map<String, String> values = new HashMap<>();
        Path path = Path.of("local.env");
        if (!Files.exists(path)) {
            return values;
        }

        try {
            for (String line : Files.readAllLines(path)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                int separator = trimmed.indexOf('=');
                if (separator <= 0) {
                    continue;
                }

                String name = trimmed.substring(0, separator).trim();
                String value = trimmed.substring(separator + 1).trim();
                if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                    value = value.substring(1, value.length() - 1);
                }

                if (!name.isEmpty()) {
                    values.put(name, value);
                }
            }
        } catch (IOException ignored) {
            return values;
        }

        return values;
    }

    private static String escapeJsonString(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
