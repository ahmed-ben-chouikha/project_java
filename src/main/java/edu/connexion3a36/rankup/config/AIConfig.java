package edu.connexion3a36.rankup.config;

public final class AIConfig {

    private AIConfig() {
    }

    private static final String DEFAULT_API_KEY = "FqbPFnjXjCzgYpPDP4HSY3bcoMwows0I";
    private static final String API_KEY_ENV = "MISTRAL_API_KEY";
    private static final String PLACEHOLDER = "FqbPFnjXjCzgYpPDP4HSY3bcoMwows0I";

    public static final String API_URL = "https://api.mistral.ai/v1/chat/completions";
    public static final String MODEL = "mistral-large-latest";
    public static final double TEMPERATURE = 0.7;
    public static final int MAX_TOKENS = 1000;

    public static final String SYSTEM_PROMPT =
            "You are an expert eSports punishment and ban recommendation assistant. " +
            "Your role is to help admins determine appropriate bans and punishments for eSports violations.";

    public static final String REWARD_MOTIF_PROMPT =
            "Tu aides à rédiger un motif de demande de récompense pour une plateforme e-sport. " +
            "Écris en français, de façon professionnelle, claire et convaincante. " +
            "Le motif doit rester honnête, concret et suffisamment détaillé pour justifier la demande. " +
            "Réponds uniquement avec le texte final du motif, sans introduction ni liste.";

    public static final String URGENCY_PROMPT =
            "You are an AI sentiment and urgency analyst for eSports reclamations. " +
            "Analyze the provided reclamation description and determine the urgency level, sentiment, summary, and recommendation.";

    public static boolean isConfigured() {
        String apiKey = getResolvedApiKey();
        return apiKey != null && !apiKey.isBlank() && !PLACEHOLDER.equals(apiKey);
    }

    public static String getApiKey() {
        String apiKey = getResolvedApiKey();
        if (apiKey == null || apiKey.isBlank() || PLACEHOLDER.equals(apiKey)) {
            throw new IllegalStateException(
                    "Mistral AI API key is not configured. Set MISTRAL_API_KEY or update DEFAULT_API_KEY in AIConfig.java."
            );
        }
        return apiKey;
    }

    private static String getResolvedApiKey() {
        String envKey = System.getenv(API_KEY_ENV);
        if (envKey != null && !envKey.isBlank()) {
            return envKey.trim();
        }
        return DEFAULT_API_KEY;
    }
}
