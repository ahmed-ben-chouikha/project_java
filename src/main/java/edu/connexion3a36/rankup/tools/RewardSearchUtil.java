package edu.connexion3a36.rankup.tools;

import java.text.Normalizer;

public final class RewardSearchUtil {

    private RewardSearchUtil() {
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.toLowerCase().replaceAll("\\s+", " ").trim();
    }

    public static int score(String query, String... fields) {
        String q = normalize(query);
        if (q.isEmpty()) {
            return 1;
        }

        int score = 0;
        String[] tokens = q.split(" ");
        for (String token : tokens) {
            if (token.isBlank()) {
                continue;
            }
            for (String field : fields) {
                if (normalize(field).contains(token)) {
                    score++;
                    break;
                }
            }
        }

        for (String field : fields) {
            if (normalize(field).contains(q)) {
                score += 2;
                break;
            }
        }

        return score;
    }

    public static boolean matches(String query, String... fields) {
        return score(query, fields) > 0;
    }
}

