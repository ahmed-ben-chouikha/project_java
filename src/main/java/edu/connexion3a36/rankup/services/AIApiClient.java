package edu.connexion3a36.rankup.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.connexion3a36.rankup.config.AIConfig;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import java.util.ArrayList;
import java.util.List;

/**
 * AI API Client for Mistral AI-powered ban recommendation chatbot
 * Uses Mistral AI's Chat API for intelligent responses
 * 
 * Mistral AI API Documentation: https://docs.mistral.ai/
 */
public class AIApiClient {
    
    private static final Gson gson = new Gson();
    private static final HttpClient httpClient = HttpClients.createDefault();
    
    // Keep conversation history for context
    private static List<JsonObject> messageHistory = new ArrayList<>();
    
    /**
     * Send a message to Mistral AI API and get response with conversation context
     * @param userMessage The user's message
     * @return AI's response from Mistral
     */
    public static String sendMessage(String userMessage) throws Exception {
        return callMistralApi(AIConfig.SYSTEM_PROMPT, userMessage, true);
    }

    /**
     * One-shot analysis for reclamation urgency and sentiment
     * @param description The reclamation description
     * @return AI's analysis
     */
    public static String analyzeUrgency(String description) throws Exception {
        return callMistralApi(AIConfig.URGENCY_PROMPT, description, false);
    }

    private static String callMistralApi(String systemPrompt, String userMessage, boolean useHistory) throws Exception {
        if (!AIConfig.isConfigured()) {
            return "❌ Mistral AI API not configured. Please add your Mistral API key to AIConfig.java";
        }
        
        JsonObject request = new JsonObject();
        request.addProperty("model", AIConfig.MODEL);
        request.addProperty("temperature", AIConfig.TEMPERATURE);
        request.addProperty("max_tokens", AIConfig.MAX_TOKENS);
        
        JsonArray messages = new JsonArray();
        
        // System message
        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", systemPrompt);
        messages.add(systemMsg);
        
        if (useHistory) {
            // Add previous conversation history (keep last 10 messages)
            int startIndex = Math.max(0, messageHistory.size() - 10);
            for (int i = startIndex; i < messageHistory.size(); i++) {
                messages.add(messageHistory.get(i));
            }
        }
        
        // Current user message
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userMessage);
        messages.add(userMsg);
        
        if (useHistory) {
            messageHistory.add(userMsg);
        }
        
        request.add("messages", messages);
        
        HttpPost httpPost = new HttpPost(AIConfig.API_URL);
        httpPost.setHeader("Authorization", "Bearer " + AIConfig.getApiKey());
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(request.toString(), java.nio.charset.StandardCharsets.UTF_8));
        
        String response = httpClient.execute(httpPost, httpResponse -> {
            return EntityUtils.toString(httpResponse.getEntity());
        });
        
        JsonObject responseObj = gson.fromJson(response, JsonObject.class);
        
        if (responseObj.has("error")) {
            JsonObject error = responseObj.getAsJsonObject("error");
            return "❌ Mistral API Error: " + (error.has("message") ? error.get("message").getAsString() : "Unknown error");
        }
        
        JsonArray choices = responseObj.getAsJsonArray("choices");
        if (choices != null && choices.size() > 0) {
            JsonObject choice = choices.get(0).getAsJsonObject();
            JsonObject message = choice.getAsJsonObject("message");
            String assistantResponse = message.get("content").getAsString();
            
            if (useHistory) {
                JsonObject assistantMsg = new JsonObject();
                assistantMsg.addProperty("role", "assistant");
                assistantMsg.addProperty("content", assistantResponse);
                messageHistory.add(assistantMsg);
            }
            
            return assistantResponse;
        }
        
        return "❌ Unexpected response format from Mistral API";
    }
    
    /**
     * Clear conversation history (for new conversation)
     */
    public static void clearHistory() {
        messageHistory.clear();
    }
    
    /**
     * Test the Mistral API connection
     * @return true if API is working, false otherwise
     */
    public static boolean testConnection() {
        try {
            String response = sendMessage("Test: Say 'Connection successful'");
            return !response.contains("❌");
        } catch (Exception e) {
            return false;
        }
    }
}


