package edu.connexion3a36.tools;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenAIToxicityChecker {
  ;String apiKey = System.getenv("OPENAI_API_KEY");
    private static final String API_URL = "https://api.openai.com/v1/moderations";

    public static boolean isToxic(String text) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String escaped = text.replace("\\", "\\\\").replace("\"", "\\\"");
        String payload = String.format("{\"model\":\"omni-moderation-latest\",\"input\": \"%s\"}", escaped);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = payload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int code = conn.getResponseCode();
        if (code == 429 || code >= 500) {
            StringBuilder errorResponse = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    errorResponse.append(line.trim());
                }
            } catch (Exception ignored) {
            }
            System.err.println("OpenAI toxicity check unavailable (" + code + "): " + errorResponse);
            return false;
        }

        if (code != 200) {
            throw new IOException("OpenAI API error: " + code);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
        }

        // Simple check for flagged: true in response
        return response.toString().contains("\"flagged\":true");
    }
}
