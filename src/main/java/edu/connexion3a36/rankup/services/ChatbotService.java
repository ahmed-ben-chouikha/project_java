package edu.connexion3a36.rankup.services;

/**
 * Simple rule-based chatbot service for punishment recommendations.
 * This implementation works offline and does not require an external API key.
 * To enable an LLM-backed advisor later, update this class to call the provider and
 * read the key from environment variable CHATBOT_API_KEY (instructions in README_CHATBOT.md).
 */
public class ChatbotService {

    public String ask(String input) {
        if (input == null || input.isBlank()) {
            return "Please provide details about the incident (offense type, context, evidence, repeat history).";
        }

        String normalized = input.toLowerCase();

        // Heuristic rules
        if (normalized.contains("cheat") || normalized.contains("hacker") || normalized.contains("explo")) {
            return buildSuggestion("PERMANENT_BAN", "High confidence: cheating/hacking detected based on description. Recommend permanent ban and investigation.");
        }

        if (normalized.contains("racist") || normalized.contains("slur") || normalized.contains("insult") || normalized.contains("threat")) {
            // check for repeat
            if (normalized.matches(".*(again|repeat|2 warnings|previous|prior).*")) {
                return buildSuggestion("TEMP_BAN", "Strongly consider a long temporary ban (7-30 days). Evidence and repeat offenses increase severity.");
            }
            return buildSuggestion("TEMP_BAN", "Medium confidence: abusive language/harassment. Recommend temporary ban (3-7 days) or warning depending on evidence.");
        }

        if (normalized.contains("payment") || normalized.contains("fraud") || normalized.contains("chargeback") || normalized.contains("scam")) {
            return buildSuggestion("TEMP_BAN_INVESTIGATE", "Potential fraud/payment issue. Recommend temporary suspension pending investigation and financial review.");
        }

        if (normalized.contains("first time") || normalized.contains("first-offense") || normalized.contains("first-offence")) {
            return buildSuggestion("WARNING", "Low severity: first offense. Recommend warning with monitoring and require apology or education.");
        }

        if (normalized.contains("spam") || normalized.contains("advertis")) {
            return buildSuggestion("WARNING_OR_SHORT_BAN", "Spam/advertising: recommend warning or short suspension (24-72 hours) depending on volume.");
        }

        // fallback: ask clarifying questions
        return "I couldn't classify the incident confidently. Please provide: offense type (cheating/abuse/fraud), whether this is a repeat offense, and evidence available (screenshots/logs).";
    }

    private String buildSuggestion(String type, String explanation) {
        return String.format("Suggestion: %s\nReason: %s", type, explanation);
    }
}


