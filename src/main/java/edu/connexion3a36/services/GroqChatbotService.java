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

public class GroqChatbotService {

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String[] MODEL_FALLBACKS = {
            "llama-3.1-70b-versatile",
            "llama-3.1-8b-instant",
            "llama-3.3-70b-versatile"
    };
    private static final Map<String, String> LOCAL_ENV = loadLocalEnv();

    private GroqChatbotService() {
    }

    public static String chat(String userMessage, String teamName, float allocatedBudget, 
                               float usedBudget, float remainingBudget, int expenseCount) throws Exception {
        String apiKey = resolveConfig("GROQ_API_KEY");
        if (apiKey.isBlank()) {
            throw new IllegalStateException("GROQ_API_KEY is not set.");
        }

        // Build system prompt with context
        String systemPrompt = buildSystemPrompt(teamName, allocatedBudget, usedBudget, remainingBudget, expenseCount);
        String[] modelCandidates = resolveModelCandidates();
        HttpClient client = HttpClient.newHttpClient();
        RuntimeException lastModelError = null;

        for (String model : modelCandidates) {
            String jsonBody = buildGroqPayload(userMessage, systemPrompt, model);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(GROQ_API_URL))
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .header("authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return parseGroqResponse(response.body());
            }

            if (isModelNotFoundError(response)) {
                lastModelError = new RuntimeException("Groq model unavailable: " + model + " - " + response.body());
                continue;
            }

            throw new RuntimeException("Groq API error: " + response.statusCode() + " - " + response.body());
        }

        if (lastModelError != null) {
            throw lastModelError;
        }

        throw new RuntimeException("Groq API error: no model candidates available.");
    }

    private static String buildSystemPrompt(String teamName, float allocatedBudget, float usedBudget,
                                             float remainingBudget, int expenseCount) {
        float usedPercent = allocatedBudget > 0 ? (usedBudget / allocatedBudget) * 100f : 0f;
        float burnRate = allocatedBudget > 0 ? (usedPercent / 100f) : 0f;
        String healthStatus = usedPercent < 50 ? "✓ Bon" : usedPercent < 80 ? "⚠ Attention" : "✗ Critique";
        
        return String.format(
            "Tu es un assistant IA EXPERT en gestion budgétaire pour les équipes esports et de sport d'équipe. " +
            "Ton rôle est d'être un conseiller stratégique intelligent qui aide les managers à:\n" +
            "1. Optimiser leurs dépenses intelligemment\n" +
            "2. Prévoir les problèmes budgétaires avant qu'ils ne surviennent\n" +
            "3. Identifier des opportunités d'économies sans compromettre la qualité\n" +
            "4. Prendre des décisions budgétaires éclairées\n\n" +
            "CONTEXTE ACTUEL DE L'ÉQUIPE:\n" +
            "🎯 Équipe: %s\n" +
            "💰 Budget alloué: %.2f €\n" +
            "📊 Budget utilisé: %.2f € (%.1f%% consommé)\n" +
            "🔄 Taux de dépense (burn rate): %.1f%% du budget par période\n" +
            "✅ Budget restant: %.2f €\n" +
            "📈 Nombre de dépenses: %d\n" +
            "🏥 Santé budgétaire: %s\n\n" +
            "CAPACITÉS:\n" +
            "- Analyser les patterns de dépenses\n" +
            "- Identifier les catégories coûteuses\n" +
            "- Prédire les dépassements de budget\n" +
            "- Suggérer des optimisations par catégorie (salaire, équipement, voyage, etc)\n" +
            "- Recommander des priorités d'investissement\n" +
            "- Calculer l'impact financier de décisions\n\n" +
            "DIRECTIVES DE COMMUNICATION:\n" +
            "✓ Sois professionnel, clair et constructif\n" +
            "✓ Fournis des chiffres précis et des explications\n" +
            "✓ Donne des recommandations pratiques et applicables\n" +
            "✓ Explique tes analyses en langage accessible\n" +
            "✓ Réponds UNIQUEMENT aux questions liées au budget, dépenses et finances de cette équipe\n" +
            "✗ Si une question n'a rien à voir avec le budget, demande poliment de rediriger vers un sujet budgétaire\n" +
            "✗ Ne fais pas de promesses impossibles ou de conseils dangereux\n" +
            "Réponds TOUJOURS en français de manière professionnelle et bienveillante.",
            teamName,
            allocatedBudget,
            usedBudget,
            usedPercent,
            burnRate,
            remainingBudget,
            expenseCount,
            healthStatus
        );
    }

    private static String buildGroqPayload(String userMessage, String systemPrompt, String model) {
        String escapedMessage = escapeJsonString(userMessage);
        String escapedSystem = escapeJsonString(systemPrompt);
        
        return String.format(
            "{\"model\":\"%s\"," +
            "\"messages\":[" +
            "{\"role\":\"system\",\"content\":\"%s\"}," +
            "{\"role\":\"user\",\"content\":\"%s\"}" +
            "]," +
            "\"max_tokens\":1024," +
            "\"temperature\":0.7}",
            escapeJsonString(model),
            escapedSystem,
            escapedMessage
        );
    }

    private static String[] resolveModelCandidates() {
        String configuredModel = resolveConfig("GROQ_MODEL");
        if (!configuredModel.isBlank()) {
            return new String[] { configuredModel };
        }
        return MODEL_FALLBACKS;
    }

    private static boolean isModelNotFoundError(HttpResponse<String> response) {
        if (response == null) {
            return false;
        }

        String body = response.body();
        if (response.statusCode() == 404) {
            return true;
        }

        if (body == null) {
            return false;
        }

        return body.contains("model_not_found")
                || body.contains("does not exist or you do not have access to it")
                || body.contains("The model");
    }

    private static String parseGroqResponse(String jsonResponse) {
        try {
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                return "Désolé, j'ai reçu une réponse vide. Veuillez réessayer.";
            }
            
            System.out.println("[GROQ] Response length: " + jsonResponse.length());
            
            // Extract content from choices[0].message.content using more robust parsing
            int choicesIndex = jsonResponse.indexOf("\"choices\"");
            if (choicesIndex == -1) {
                System.err.println("[GROQ] No 'choices' field found in response");
                return "Désolé, je n'ai pas pu traiter votre demande (format invalide).";
            }
            
            int messageIndex = jsonResponse.indexOf("\"message\"", choicesIndex);
            if (messageIndex == -1) {
                System.err.println("[GROQ] No 'message' field found in response");
                return "Désolé, je n'ai pas pu traiter votre demande (pas de message).";
            }
            
            int contentIndex = jsonResponse.indexOf("\"content\"", messageIndex);
            if (contentIndex == -1) {
                System.err.println("[GROQ] No 'content' field found in response");
                return "Désolé, je n'ai pas pu traiter votre demande (pas de contenu).";
            }
            
            // Find the opening quote of the content value
            int quoteStart = jsonResponse.indexOf("\"", contentIndex + 10);
            if (quoteStart == -1) {
                System.err.println("[GROQ] Could not find content string start");
                return "Désolé, je n'ai pas pu traiter votre demande.";
            }
            
            // Find the closing quote, accounting for escaped quotes
            int quoteEnd = quoteStart + 1;
            while (quoteEnd < jsonResponse.length()) {
                char c = jsonResponse.charAt(quoteEnd);
                if (c == '\\' && quoteEnd + 1 < jsonResponse.length()) {
                    quoteEnd += 2; // Skip escaped character
                } else if (c == '"') {
                    break; // Found closing quote
                } else {
                    quoteEnd++;
                }
            }
            
            if (quoteEnd >= jsonResponse.length()) {
                System.err.println("[GROQ] Could not find content string end");
                return "Désolé, je n'ai pas pu traiter votre demande.";
            }
            
            String content = jsonResponse.substring(quoteStart + 1, quoteEnd);
            
            // Unescape JSON string properly
            content = unescapeJsonString(content);
            
            System.out.println("[GROQ] Successfully parsed response: " + content.substring(0, Math.min(100, content.length())));
            return content;
            
        } catch (Exception e) {
            System.err.println("[GROQ] Error parsing response: " + e.getMessage());
            e.printStackTrace();
            return "Désolé, une erreur technique est survenue lors du traitement.";
        }
    }
    
    private static String unescapeJsonString(String str) {
        if (str == null) return "";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\\' && i + 1 < str.length()) {
                char next = str.charAt(i + 1);
                switch (next) {
                    case '"': result.append('"'); i++; break;
                    case '\\': result.append('\\'); i++; break;
                    case '/': result.append('/'); i++; break;
                    case 'b': result.append('\b'); i++; break;
                    case 'f': result.append('\f'); i++; break;
                    case 'n': result.append('\n'); i++; break;
                    case 'r': result.append('\r'); i++; break;
                    case 't': result.append('\t'); i++; break;
                    case 'u': 
                        if (i + 5 < str.length()) {
                            String hexCode = str.substring(i + 2, i + 6);
                            try {
                                result.append((char) Integer.parseInt(hexCode, 16));
                                i += 5;
                            } catch (NumberFormatException e) {
                                result.append(c);
                            }
                        } else {
                            result.append(c);
                        }
                        break;
                    default: result.append(c); break;
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private static String resolveConfig(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = System.getProperty(key);
        }
        if ((value == null || value.isBlank()) && LOCAL_ENV.containsKey(key)) {
            value = LOCAL_ENV.get(key);
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
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
}
